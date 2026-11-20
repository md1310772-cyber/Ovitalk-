OVITALK ANDROID PROJECT — প্রাথমিক সোর্স প্রজেক্ট

গুরুত্বপূর্ণ:
এটি এখনো সম্পূর্ণ/প্রকাশযোগ্য অ্যাপ নয় এবং APK ফাইল নয়। এটি Android source project।
Firebase-এর সঙ্গে Phone OTP লগইনের প্রাথমিক সংযোগের কোড আছে। নিরাপদ private chat,
বন্ধু খোঁজা, push notification, ছবি পাঠানো, abuse reporting এবং production testing
এখনো সম্পূর্ণ করা বাকি।

APK build করার আগে:
1) Firebase Console > Project settings > Your apps > Add app > Android নির্বাচন করুন।
2) Android package name দিন: com.ovitalk.app
3) Firebase থেকে google-services.json ডাউনলোড করে app/ ফোল্ডারে রাখুন।
4) Phone Authentication চালু রাখুন।
5) Firebase Console-এ Android app-এর SHA-1 ও SHA-256 fingerprint যোগ করতে হবে।
6) Firestore Rules অবশ্যই chat membership যাচাই করবে। আগের সরল rules production-এ নিরাপদ নয়।
7) এরপর Android Gradle build চালাতে হবে।

এই প্রজেক্টে কোনো payment বা card information নেই।
কোনো card number, OTP বা password কারও সঙ্গে শেয়ার করবেন না।

ফোন নম্বর লগইন Firebase-এর SMS quota/দেশভিত্তিক নিয়মের অধীন হতে পারে।
