import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-app.js';
import { getAuth } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-auth.js';
import { getFirestore, collection, addDoc, doc, runTransaction, serverTimestamp } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-firestore.js';
import { commonHabits } from './habits.js';

fetch('/.netlify/functions/firebase-config')
  .then(response => response.json())
  .then(firebaseConfig => {
    const firebaseApp = initializeApp(firebaseConfig);
    const db = getFirestore(firebaseApp);
    const auth = getAuth(firebaseApp);

    const addHabitForm = document.getElementById('addHabitForm');
    const habitNameInput = document.getElementById('habitName');
    const habitSuggestions = document.getElementById('habitSuggestions');

    habitNameInput.addEventListener('input', () => {
        const inputText = habitNameInput.value.toLowerCase();
        habitSuggestions.innerHTML = '';
        if (inputText.length >= 3) {
            const suggestions = commonHabits.filter(habit => habit.toLowerCase().includes(inputText));
            suggestions.forEach(suggestion => {
                const suggestionElement = document.createElement('div');
                suggestionElement.textContent = suggestion;
                suggestionElement.addEventListener('click', () => {
                    habitNameInput.value = suggestion;
                    habitSuggestions.innerHTML = '';
                });
                habitSuggestions.appendChild(suggestionElement);
            });
        }
    });

    addHabitForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const currentUser = auth.currentUser;
        const urlParams = new URLSearchParams(window.location.search);
        const goalId = urlParams.get('goalId');

        if (currentUser && goalId) {
            const habitName = habitNameInput.value;
            const description = document.getElementById('description').value;
            const currentConfidence = document.getElementById('currentConfidence').value;
            const targetLevel = document.getElementById('targetLevel').value;
            const feelings = document.getElementById('feelings').value;
            const repetitions = document.getElementById('repetitions').value;

            const habitRef = await addDoc(collection(db, 'users', currentUser.uid, 'goals', goalId, 'habits'), {
                name: habitName,
                description: description,
                currentConfidence: currentConfidence,
                targetLevel: targetLevel,
                feelings: feelings,
                repetitions: repetitions,
                happyMonkeyCount: 1,
                createdAt: serverTimestamp()
            });

            const goalRef = doc(db, 'users', currentUser.uid, 'goals', goalId);
            await runTransaction(db, async (transaction) => {
                const goalDoc = await transaction.get(goalRef);
                const newScore = (goalDoc.data().score || 0) + 1;
                transaction.update(goalRef, { score: newScore });
            });

            window.location.href = '/';
        } else {
            alert('You must be logged in and have a goal to add a habit.');
        }
    });
  });
