<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="csrf-token" content="{{ csrf_token() }}">

        <title>{{ config('app.name', 'Laravel') }}</title>

        <!-- Fonts -->
        <link rel="stylesheet" href="https://fonts.bunny.net/css2?family=Nunito:wght@400;600;700&display=swap">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.3.0/font/bootstrap-icons.css" />

        <!-- Scripts -->
        @vite(['resources/js/app.js'])
        @spladeHead

    </head>
    <body class="font-sans antialiased">
        @splade

        <!-- The core Firebase JS SDK is always required and must be listed first -->
        <script src="https://www.gstatic.com/firebasejs/8.3.2/firebase-app.js"></script>
        <script src="https://www.gstatic.com/firebasejs/8.3.2/firebase-messaging.js"></script>

        <!-- TODO: Add SDKs for Firebase products that you want to use
            https://firebase.google.com/docs/web/setup#available-libraries -->

        <script>
            // Your web app's Firebase configuration
            const firebaseConfig = {
                apiKey: "YOUR-FIREBASE-WEB-API-KEY-HERE",
                authDomain: "enurse-app-placeholder.firebaseapp.com",
                projectId: "enurse-app-placeholder",
                storageBucket: "enurse-app-placeholder.appspot.com",
                messagingSenderId: "000000000000",
                appId: "1:000000000000:web:5050eece07a171c5e740e9",
                measurementId: "G-0LDEE5HXXG"
            };
            // Initialize Firebase
            const app = firebase.initializeApp(firebaseConfig);

            const messaging = firebase.messaging();

            function initFirebaseMessagingRegistration() {
                messaging.requestPermission().then(function () {
                    return messaging.getToken()
                }).then(function(token) {

                    axios.post("{{ route('nurses.fcmtoken') }}",{
                        _method:"PATCH",
                        token
                    }).then(({data})=>{
                        console.log(data)
                    }).catch(({response:{data}})=>{
                        console.error(data)
                    })

                }).catch(function (err) {
                    console.log(`Token Error :: ${err}`);
                });
            }

            initFirebaseMessagingRegistration();

            messaging.onMessage(function({data:{body,title}}){
                new Notification(title, {body});
            });
        </script>
    </body>
</html>
