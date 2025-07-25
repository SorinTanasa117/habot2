const app = document.getElementById('app');

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "your_api_key",
  authDomain: "habittracker-12345.firebaseapp.com",
  projectId: "habittracker-12345",
  storageBucket: "habittracker-12345.appspot.com",
  messagingSenderId: "1234567890",
  appId: "1:1234567890:web:1234567890abcdef"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const db = firebase.firestore();
const auth = firebase.auth();

let currentUser = null;

auth.onAuthStateChanged(user => {
    if (user) {
        currentUser = user;
        renderApp();
    } else {
        renderLogin();
    }
});

function renderLogin() {
    app.innerHTML = `
        <button id="loginButton">Login with Google</button>
    `;
    const loginButton = document.getElementById('loginButton');
    loginButton.addEventListener('click', () => {
        const provider = new firebase.auth.GoogleAuthProvider();
        auth.signInWithPopup(provider);
    });
}

function renderApp() {
    app.innerHTML = `
        <button id="logoutButton">Logout</button>
        <div id="goals"></div>
        <button id="addGoalButton">Add Goal</button>
    `;
    const logoutButton = document.getElementById('logoutButton');
    logoutButton.addEventListener('click', () => auth.signOut());

    const addGoalButton = document.getElementById('addGoalButton');
    addGoalButton.addEventListener('click', () => {
        const goalName = prompt('Enter goal name:');
        if (goalName) {
            db.collection('users').doc(currentUser.uid).collection('goals').add({
                name: goalName,
                score: 1,
                createdAt: firebase.firestore.FieldValue.serverTimestamp()
            });
        }
    });

    const goalsContainer = document.getElementById('goals');
    db.collection('users').doc(currentUser.uid).collection('goals').orderBy('createdAt', 'desc').onSnapshot(snapshot => {
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
            db.collection('users').doc(currentUser.uid).collection('goals').doc(doc.id).collection('habits').orderBy('createdAt', 'desc').onSnapshot(habitsSnapshot => {
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

document.addEventListener('click', event => {
    if (event.target.matches('.addHabitButton')) {
        const goalId = event.target.dataset.goalId;
        const habitName = prompt('Enter habit name:');
        if (habitName) {
            db.collection('users').doc(currentUser.uid).collection('goals').doc(goalId).collection('habits').add({
                name: habitName,
                happyMonkeyCount: 1,
                createdAt: firebase.firestore.FieldValue.serverTimestamp()
            });
        }
    }

    if (event.target.matches('.iDidItButton')) {
        const goalId = event.target.dataset.goalId;
        const habitId = event.target.dataset.habitId;
        const habitRef = db.collection('users').doc(currentUser.uid).collection('goals').doc(goalId).collection('habits').doc(habitId);
        db.runTransaction(async transaction => {
            const habitDoc = await transaction.get(habitRef);
            const newHappyMonkeyCount = (habitDoc.data().happyMonkeyCount || 0) + 1;
            transaction.update(habitRef, { happyMonkeyCount: newHappyMonkeyCount });

            const goalRef = db.collection('users').doc(currentUser.uid).collection('goals').doc(goalId);
            const goalDoc = await transaction.get(goalRef);
            const newScore = (goalDoc.data().score || 0) + 1;
            transaction.update(goalRef, { score: newScore });
        });
    }
});
