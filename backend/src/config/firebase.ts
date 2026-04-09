import * as admin from "firebase-admin";
import * as path from "path";

const serviceAccountPath = path.resolve(
  __dirname,
  "../../mountain-penguin-firebase-adminsdk-fbsvc-95c9920ab0.json"
);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccountPath),
});

export const db = admin.firestore();
