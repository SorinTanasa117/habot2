const app = document.getElementById('app');

fetch('/.netlify/functions/firebase-config')
  .then(response => response.json())
  .then(firebaseConfig => {
    // Initialize Firebase
    firebase.initializeApp(firebaseConfig);
    const db = firebase.firestore();
    const auth = firebase.auth();

    let currentUser = null;

    auth.onAuthStateChanged(user => {
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
        <div class="login-container">
            <button id="loginButton" class="login-button">
                <img src="https://developers.google.com/identity/images/g-logo.png" alt="Google logo">
                Login with Google
            </button>
            <a href="/register.html" class="button">Register with Email</a>
        </div>
    `;
    const loginButton = document.getElementById('loginButton');
    loginButton.addEventListener('click', () => {
        const provider = new firebase.auth.GoogleAuthProvider();
        auth.signInWithPopup(provider);
    });
}

function renderApp(db, auth, currentUser) {
    app.innerHTML = `
        <div class="header">
            <h1>Habit Tracker</h1>
            <div>
                <a href="/profile.html" class="button">Profile</a>
                <button id="logoutButton">Logout</button>
            </div>
        </div>
        <div id="goals"></div>
        <a href="/add-goal.html" class="button">Add Goal</a>
    `;
    const logoutButton = document.getElementById('logoutButton');
    logoutButton.addEventListener('click', () => auth.signOut());

    const goalsContainer = document.getElementById('goals');
    db.collection('users').doc(currentUser.uid).collection('goals').orderBy('createdAt', 'desc').onSnapshot(snapshot => {
        goalsContainer.innerHTML = '';
        snapshot.forEach(doc => {
            const goal = doc.data();
            const goalElement = document.createElement('div');
            goalElement.className = 'goal';
            goalElement.innerHTML = `
                <h3>${goal.name}</h3>
                <p>Score: ${goal.score} 🐵</p>
                <div class="habits"></div>
                <a href="/add-habit.html?goalId=${doc.id}" class="button addHabitButton" data-goal-id="${doc.id}">Add Habit</a>
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
    if (event.target.matches('.iDidItButton')) {
        const goalId = event.target.dataset.goalId;
        const habitId = event.target.dataset.habitId;

        const confirmed = confirm("Are you sure you did it? Remember, sticking to your resolve to do the thing you set up is what's important, not tickling a monkey button once a day. Believe in yourself!");

        if (confirmed) {
            const db = firebase.firestore();
            const auth = firebase.auth();
            const currentUser = auth.currentUser;
            const habitRef = db.collection('users').doc(currentUser.uid).collection('goals').doc(goalId).collection('habits').doc(habitId);

            db.runTransaction(async transaction => {
                const habitDoc = await transaction.get(habitRef);
                const newHappyMonkeyCount = (habitDoc.data().happyMonkeyCount || 0) + 1;
                transaction.update(habitRef, { happyMonkeyCount: newHappyMonkeyCount });

                const goalRef = db.collection('users').doc(currentUser.uid).collection('goals').doc(goalId);
                const goalDoc = await transaction.get(goalRef);
                const newScore = (goalDoc.data().score || 0) + 1;
                transaction.update(goalRef, { score: newScore });

                alert(`Alright! Great job! You're just ${20 - newHappyMonkeyCount % 20} away from nailing this habit.`);
            });
        }
    }
});
