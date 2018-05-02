# Drug-Dispenser

![build status](https://circleci.com/gh/martypv/Drug-Dispenser.png?circle-token=circle-token "Master Build Status")

Waffle.io: https://waffle.io/martypv/Drug-Dispenser
<br>Slack Channel: https://groupfiveworkspace.slack.com/messages/C9JK5TYNN
<br>Testing Plan: https://docs.google.com/document/d/1YNFnGZTy0KBOUsWMn6S1iK5du5MSE6HpJ8hAyv4N0-4/edit?usp=sharing
<br>Narratives and Scenarios: https://docs.google.com/document/d/1Q_dk9RychVNPzYsZbdGBjbsRzND30ZOjMLG0b2xb7Vc/edit
<br>Sprint Goal #1: Ability to create different types of users and to create/validate orders
<br>Sprint 1 Diagram: https://drive.google.com/file/d/1etFX3j10tte2eIVCnJCb-t-Zxm8QimKj/view?usp=sharing

<br>Client Goal: Ability to log in as a different user and see/access order
<br>Sprint 1 Review Doc: https://docs.google.com/document/d/1KYaCfnkpaszGC_eCHUknwHxCvao6SKyU7CCthwG4o6Q/edit?usp=sharing
<br>Sprint 1 Retrospective: https://docs.google.com/document/d/1z5SASLC31TXuWqaEGOKKDKgIloSR0CjPSRMXJ44hDQg/edit

<br>Sprint 2 Goal: The system has interactivity and can support transactions
<br>Sprint 2 State Diagram: https://drive.google.com/open?id=1qyjgxeytqWew2RhaAJnpnFJAqLp2yGnJ
<br>Sprint 2 Review Doc: https://docs.google.com/document/d/1CHM98vUjxuQtrUBdtoz8HD5juXbX8U5FbObUcq-8LH8/edit?usp=sharing
<br>Sprint 2 Retrospective: https://docs.google.com/document/d/1myjvUHm9BOD74lS6Shqmdu2_smhABLwcYpXpXwvokUA/edit?usp=sharing

<br>Sprint 3 Goal: Deliver a working and optimized system
<br> Reverse Engineering Group 2: https://docs.google.com/document/d/1vBz5_DFn_ssJsdFNEi-g9W50_abMPLqkHvzcnlAW48E/edit?usp=sharing

## Setup Notes

Create two MySQL databases: one for testing and one for production. Seed both of these with the most recent sql script in `src/main/sql/`.
<br>Copy `src/main/java/edu.ithaca.group5/ConfigIn.java` to `src/main/java/edu.ithaca.group5/Config.java` and replace constants with proper information.
