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

    document.addEventListener('click', async event => {
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
                const goalRef = doc(db, 'users', currentUser.uid, 'goals', goalId);
                const habitDoc = await transaction.get(habitRef);
                const goalDoc = await transaction.get(goalRef);

                const newHappyMonkeyCount = (habitDoc.data().happyMonkeyCount || 0) + 1;
                const newScore = (goalDoc.data().score || 0) + 1;

                transaction.update(habitRef, { happyMonkeyCount: newHappyMonkeyCount });
                transaction.update(goalRef, { score: newScore });
            });
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
        <div class="header">
            <h1>Monkey See</h1>
            <div>
                <a href="/profile.html" class="button">Profile</a>
                <button id="logoutButton">Logout</button>
            </div>
        </div>
        <div id="goals"></div>
    `;
    const logoutButton = document.getElementById('logoutButton');
    logoutButton.addEventListener('click', () => signOut(auth));

    const goalsContainer = document.getElementById('goals');
    const goalsQuery = query(collection(db, 'users', currentUser.uid, 'goals'), orderBy('createdAt', 'desc'));
    onSnapshot(goalsQuery, snapshot => {
        goalsContainer.innerHTML = '';
        snapshot.forEach(doc => {
            const goal = doc.data();
            const happyApes = Math.floor(goal.score / 20);
            const goalElement = document.createElement('div');
            goalElement.className = 'goal';
            goalElement.innerHTML = `
                <div class="goal-header">
                    <h3>${goal.name}</h3>
                    <p>Happy Apes: ${'🦍'.repeat(happyApes)}</p>
                </div>
                <p>Monkey dos: ${'🐵'.repeat(goal.score)}</p>
                <div class="habits" style="display: none;"></div>
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

            goalElement.querySelector('.goal-header').addEventListener('click', () => {
                const habits = goalElement.querySelector('.habits');
                habits.style.display = habits.style.display === 'none' ? 'block' : 'none';
            });
        });
    });
}
