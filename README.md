# AML Notes App (Kotlin)

This project is a fully-featured **Notes app** built with Kotlin. It allows users to create notes with the ability to:
- Add **images** within the notes.
- Create **to-dos** within the notes.
- Set up **alarm reminders** for notes, which will trigger notifications at the scheduled time.

## Features

- **Rich Notes**: 
  - Create and edit notes with embedded images.
  - Add to-dos directly within your notes, making them more interactive and useful for task tracking.

- **Reminders**:
  - Set alarm reminders for any note.
  - Receive notifications when the reminder time is reached, ensuring you never miss an important task or note.

- **Notifications**:
  - Get notified at the exact time you set for the alarm, allowing you to keep track of important events or tasks within your notes.

## Project Structure

- **Kotlin-based**: The app is developed using Kotlin for Android, ensuring concise and expressive code.
- **Alarm Manager**: Uses Android's Alarm Manager to schedule reminders and trigger notifications.
- **To-do Integration**: Allows creating actionable tasks within the notes.

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/MrAmmia/AML_Notes_kotlin.git
   cd AML_Notes_kotlin

2. **Open in Android Studio**:
   - Open Android Studio and import the project.
   - Let Android Studio sync and resolve dependencies.

3. **Build and Run**:
   - Connect an Android device or start an emulator.
   - Build the project and run it on your device.

## Screenshots

![Screenshot_20220610_115827](https://user-images.githubusercontent.com/61557175/173851441-4b437a59-dbb6-4b31-9063-715392f1b327.png)
![Screenshot_20220610_115901](https://user-images.githubusercontent.com/61557175/173851448-37e025ae-9419-40e7-9d0c-cd90ff070796.png)
![Screenshot_20220610_125724](https://user-images.githubusercontent.com/61557175/173851455-81bac307-40bf-4455-a7c1-eba8171e8c99.png)
![Screenshot_20220610_125748](https://user-images.githubusercontent.com/61557175/173851459-432c46f8-71e7-4722-95dd-753256fc5ac4.png)
![Screenshot_20220610_125805](https://user-images.githubusercontent.com/61557175/173851462-7856d598-7a65-487f-a7af-4c93990206df.png)
![Screenshot_20220610_130041](https://user-images.githubusercontent.com/61557175/173851471-5797d2f1-1ff1-439d-b894-b1ddff47905f.png)
![Screenshot_20220610_130100](https://user-images.githubusercontent.com/61557175/173851489-36ada554-7ecd-44ef-bd92-f500ee2ab900.png)


## Libraries & Tools Used

   - **Kotlin**: For development.
   - **Room Database**: For persisting the notes and to-dos locally.
   - **AlarmManager**: To schedule alarms and notifications.
   - **RecyclerView**: For displaying the notes and to-dos in a list.
   - **ViewModel & LiveData**: For managing UI-related data in a lifecycle-conscious way.
   - **Coroutines**: For asynchronous tasks.
