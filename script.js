import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-app.js';
import { getAuth, onAuthStateChanged, GoogleAuthProvider, signInWithPopup, signOut } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-auth.js';
import { getFirestore, collection, doc, addDoc, onSnapshot, orderBy, query, runTransaction, serverTimestamp } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-firestore.js';

const app = document.getElementById('app');

fetch('/.netlify/functions/firebase-config')
  .then(response => response.json())
  .then(firebaseConfig => {
    // Initialize Firebase
    const firebaseApp = initializeApp(firebaseConfig);
    const db = getFirestore(firebaseApp);
    const auth = getAuth(firebaseApp);

    let currentUser = null;

    onAuthStateChanged(auth, user => {
        if (user) {
            currentUser = user;
            renderApp(db, auth, currentUser);
        } else {
            renderLogin(auth);
        }
    });
  });

function renderLogin(auth) {
    app.innerHTML = `
        <button id="loginButton">Login with Google</button>
    `;
    const loginButton = document.getElementById('loginButton');
    loginButton.addEventListener('click', () => {
        const provider = new GoogleAuthProvider();
        signInWithPopup(auth, provider);
    });
}

function renderApp(db, auth, currentUser) {
    app.innerHTML = `
        <button id="logoutButton">Logout</button>
        <div id="goals"></div>
        <button id="addGoalButton">Add Goal</button>
    `;
    const logoutButton = document.getElementById('logoutButton');
    logoutButton.addEventListener('click', () => signOut(auth));

    const addGoalButton = document.getElementById('addGoalButton');
    addGoalButton.addEventListener('click', () => {
        const goalName = prompt('Enter goal name:');
        if (goalName) {
            addDoc(collection(db, 'users', currentUser.uid, 'goals'), {
                name: goalName,
                score: 1,
                createdAt: serverTimestamp()
            });
        }
    });

    const goalsContainer = document.getElementById('goals');
    const goalsQuery = query(collection(db, 'users', currentUser.uid, 'goals'), orderBy('createdAt', 'desc'));
    onSnapshot(goalsQuery, snapshot => {
        goalsContainer.innerHTML = '';
        snapshot.forEach(doc => {
            const goal = doc.data();
            const goalElement = document.createElement('div');
            goalElement.className = 'goal';
            goalElement.innerHTML = `
                <h3>${goal.name}</h3>
                <p>Score: ${goal.score}</p>
                <div class="habits"></div>
                <button class="addHabitButton" data-goal-id="${doc.id}">Add Habit</button>
            `;
            goalsContainer.appendChild(goalElement);

            const habitsContainer = goalElement.querySelector('.habits');
            const habitsQuery = query(collection(db, 'users', currentUser.uid, 'goals', doc.id, 'habits'), orderBy('createdAt', 'desc'));
            onSnapshot(habitsQuery, habitsSnapshot => {
                habitsContainer.innerHTML = '';
                habitsSnapshot.forEach(habitDoc => {
                    const habit = habitDoc.data();
                    const habitElement = document.createElement('div');
                    habitElement.className = 'habit';
                    habitElement.innerHTML = `
                        <h4>${habit.name}</h4>
                        <p>Happy Monkeys: ${habit.happyMonkeyCount} 🐵</p>
                        <button class="iDidItButton" data-goal-id="${doc.id}" data-habit-id="${habitDoc.id}">I did it!</button>
                    `;
                    habitsContainer.appendChild(habitElement);
                });
            });
        });
    });
}

document.addEventListener('click', async event => {
    const db = getFirestore();
    const auth = getAuth();
    const currentUser = auth.currentUser;

    if (event.target.matches('.addHabitButton')) {
        const goalId = event.target.dataset.goalId;
        const habitName = prompt('Enter habit name:');
        if (habitName) {
            await addDoc(collection(db, 'users', currentUser.uid, 'goals', goalId, 'habits'), {
                name: habitName,
                happyMonkeyCount: 1,
                createdAt: serverTimestamp()
            });
        }
    }

    if (event.target.matches('.iDidItButton')) {
        const goalId = event.target.dataset.goalId;
        const habitId = event.target.dataset.habitId;
        const habitRef = doc(db, 'users', currentUser.uid, 'goals', goalId, 'habits', habitId);

        await runTransaction(db, async transaction => {
            const habitDoc = await transaction.get(habitRef);
            const newHappyMonkeyCount = (habitDoc.data().happyMonkeyCount || 0) + 1;
            transaction.update(habitRef, { happyMonkeyCount: newHappyMonkeyCount });

            const goalRef = doc(db, 'users', currentUser.uid, 'goals', goalId);
            const goalDoc = await transaction.get(goalRef);
            const newScore = (goalDoc.data().score || 0) + 1;
            transaction.update(goalRef, { score: newScore });
        });
    }
});
