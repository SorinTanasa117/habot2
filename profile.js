import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-app.js';
import { getAuth } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-auth.js';
import { getFirestore, doc, getDoc } from 'https://www.gstatic.com/firebasejs/9.6.1/firebase-firestore.js';

fetch('/.netlify/functions/firebase-config')
  .then(response => response.json())
  .then(firebaseConfig => {
    const firebaseApp = initializeApp(firebaseConfig);
    const db = getFirestore(firebaseApp);
    const auth = getAuth(firebaseApp);

    const profileContainer = document.getElementById('profile');

    auth.onAuthStateChanged(async (user) => {
        if (user) {
            const userDoc = await getDoc(doc(db, 'users', user.uid));
            if (userDoc.exists()) {
                const userData = userDoc.data();
                profileContainer.innerHTML = `
                    <p><strong>Name:</strong> ${userData.name} ${userData.surname}</p>
                    <p><strong>Email:</strong> ${userData.email}</p>
                    <p><strong>Avatar:</strong> ${userData.avatar}</p>
                `;
            }
        } else {
            window.location.href = '/';
        }
    });
  });
