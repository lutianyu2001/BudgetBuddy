# App Name: BudgetBuddy

Qiming Zhang, [qzhang478@wisc.edu](mailto:qzhang478@wisc.edu), terry-qmzhang

Sky Lu, [tlu83@wisc.edu](mailto:tlu83@wisc.edu), lutianyu2001

Sanjana Golla, [ssgolla@wisc.edu](mailto:ssgolla@wisc.edu), sanjuGolla1

---

### **Introduce your App**

1. Describe what the problem is that your app is solving. Why is this an important problem (if you can provide some validation that this problem is important, it would be useful)?  
   Managing personal and household finances can be overwhelming for many people, especially for those without a strong financial background. Many existing budgeting tools are either too complicated or lack essential features, making it difficult to track income, expenses and financial health.  
     
2. What is your app: Describe clearly what the app is, and at a high level what it does. You can use some pictures if needed. Think of this as the marketing pitch for your app, why should the user care and why they should use it.  
   BudgetBuddy is a simple, user-friendly budgeting assistant designed for anyone looking to manage their personal or household finances effectively. It allows users to easily track income and expenses, categorize them, set budget limits, and view their financial health through visual graphs. Additionally, users will receive alerts if they exceed their spending limits, and at the end of each month, the app generates a summary report to help users reflect on their financial habits.  
     
3. Who are your *natural users*? You will need to interact with them a bit in the semester and collect feedback from such users.  
* College students  
* Recent graduates  
* Young professionals  
* Individuals sharing expenses (roommates)  
  


---

### **Your competition**

1. What other apps have you found that are similar to your app?  
   Money Manager Expense & Budget  
     
2. What is your experience with such other apps?  
   Personally used it to monitor expenses for a short period, making it a habit to view monthly and daily expenses. Also allowed you to add recurring charges, though many users reported facing issues with this. Involved manual data inputting as well.  
     
3. What will make your app better than such competition? What niche does it fill?  
   Difficulties with logging repeated transactions. Lack of overspending alert features, which our application’s scope covers.

---

### **Main Modules of your app**

What do you think are the main modules of your app, e.g., server-side, mobile device side, other 3rd party software or services.

1. User Interface  
* Clean, intuitive mobile interface  
* Filtering with category, location  
* Set spending limit  
* Data visualization (graphs, charts)  
* Goal tracking  
2. Data Management & Storage  
* Local storage for offline access  
* Cloud sync for data backup and multi-device access  
3. Analytics Module  
* Spending pattern analysis  
* Generate real-time report, end-of-month summary report  
4. Notification System  
* Push notifications alert for category or location spend exceed limit  
* Reminders for financial goals and bill payments  
5. Bank Integration Module  
* Connect to Bank API (Not real bank API but our sample bank backend since we do not have qualifications)  
* Pull balance  
* Pull transaction history  
6. Bank Statement Analyzer  
* Analyze bank statement pdf document for banks does not support pulling transaction history  
7. Sample Bank Backend  
* Database for storing account information, transactions  
* Provide API access for our app

---

### **Mobile “Innovation”**

What do you think is the most innovative aspect of your app from a mobile app perspective, i.e., what will you do differently from a regular desktop application that makes it truly mobile. Usually, the app should incorporate some features of a mobile device to make the experience of users quite seamless.

1. Location-based Budgeting: Use GPS to track spending in different locations and provide location-based notifications for budget recommendations and alerts of overspending.  
2. Biometric Authentication: Utilize fingerprint or face recognition for secure app access.

---

### **What you need to test your app**

What mobile devices do you need to test your project, and how many such devices do you have access to among your group members?

Devices: Android Studio VM (Pixel 6 API 34), Real android device (at least have 3 devices among group members)  
We also can recommend it to other people for testing and put out surveys for feedback.  
