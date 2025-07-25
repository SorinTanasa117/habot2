import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-app.js';
import { getAuth } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-auth.js';
import { getFirestore, collection, addDoc, serverTimestamp } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-firestore.js';

fetch('/.netlify/functions/firebase-config')
  .then(response => response.json())
  .then(firebaseConfig => {
    const firebaseApp = initializeApp(firebaseConfig);
    const db = getFirestore(firebaseApp);
    const auth = getAuth(firebaseApp);

    const addGoalForm = document.getElementById('addGoalForm');

    addGoalForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const currentUser = auth.currentUser;

        if (currentUser) {
            const habitName = document.getElementById('habitName').value;
            const currentConfidence = document.getElementById('currentConfidence').value;
            const targetLevel = document.getElementById('targetLevel').value;
            const feelings = document.getElementById('feelings').value;

            console.log("Adding goal:", {
                name: habitName,
                currentConfidence: currentConfidence,
                targetLevel: targetLevel,
                feelings: feelings,
                score: 1,
                createdAt: serverTimestamp()
            });
            await addDoc(collection(db, 'users', currentUser.uid, 'goals'), {
                name: habitName,
                currentConfidence: currentConfidence,
                targetLevel: targetLevel,
                feelings: feelings,
                score: 1,
                createdAt: serverTimestamp()
            });

            window.location.href = '/';
        } else {
            alert('You must be logged in to add a goal.');
        }
    });
  });
