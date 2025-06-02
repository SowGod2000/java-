// firebase.js
import { initializeApp } from "firebase/app";
import { getAuth, RecaptchaVerifier, signInWithPhoneNumber } from "firebase/auth";

const firebaseConfig = {
    apiKey: "AIzaSyDVjJ_2ARbLDRdQ5om3_RDbTgmW81GM2FE",
    authDomain: "springboot-89665.firebaseapp.com",
    projectId: "springboot-89665",
    storageBucket: "springboot-89665.firebasestorage.app",
    messagingSenderId: "451081384863",
    appId: "1:451081384863:web:5b542a98d2d12ebd3003aa"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

export { auth, RecaptchaVerifier, signInWithPhoneNumber };
