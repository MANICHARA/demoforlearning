# Content Blocker - Android App

A comprehensive Android application designed to block Instagram Reels and YouTube Shorts content, helping users reduce time spent on short-form videos and improve digital wellbeing.

## Features

### 🚫 Content Blocking
- **Instagram Reels Blocking**: Automatically detects and blocks Instagram Reels content
- **YouTube Shorts Blocking**: Prevents access to YouTube Shorts videos
- **Multiple Blocking Methods**: Choose between redirect to home, close app, or show alert

### 📊 Statistics & Analytics
- **Daily Statistics**: Track blocked content count and time saved per day
- **Weekly/Monthly Charts**: Visual representation of blocking patterns
- **Time Saved Tracking**: Estimate time saved from blocked content
- **Total Statistics**: Cumulative blocking data

### ⚙️ Customizable Settings
- **Individual App Control**: Enable/disable blocking for Instagram or YouTube separately
- **Notification Preferences**: Choose whether to receive blocking notifications
- **Auto-start Option**: Automatically start service on device boot
- **Blocking Method Selection**: Customize how content is blocked

### 🔒 Privacy & Permissions
- **Usage Stats Permission**: Required to detect app usage and content
- **Notification Permission**: For blocking alerts (Android 13+)
- **System Alert Window**: For overlay blocking methods

## Technical Architecture

### Core Components
- **MainActivity**: Main entry point with navigation and service control
- **ContentBlockingService**: Background service monitoring app usage
- **ContentDetector**: Utility for detecting Instagram Reels and YouTube Shorts
- **PreferenceManager**: Handles app settings and statistics
- **PermissionHelper**: Manages required permissions

### UI Components
- **HomeFragment**: Dashboard with service status and quick statistics
- **SettingsFragment**: App configuration and preferences
- **StatisticsFragment**: Detailed analytics with charts
- **Bottom Navigation**: Easy navigation between sections

### Technologies Used
- **Kotlin**: Primary programming language
- **Material Design 3**: Modern UI components
- **Navigation Component**: Fragment-based navigation
- **ViewBinding**: Type-safe view access
- **Coroutines**: Asynchronous programming
- **Room Database**: Local data storage (planned)
- **MPAndroidChart**: Statistics visualization

## Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9.10+

### Build Instructions
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Required Permissions
The app requires several permissions to function properly:

1. **Usage Access Permission**
   - Navigate to Settings > Apps > Content Blocker > Permissions
   - Enable "Usage access"
   - This is required for detecting app usage

2. **Notification Permission** (Android 13+)
   - App will request this permission on first run
   - Required for showing blocking notifications

3. **System Alert Window** (Optional)
   - For overlay-based blocking methods
   - Can be granted in app settings

## Usage Guide

### First Time Setup
1. Launch the app
2. Grant required permissions when prompted
3. Configure blocking preferences in Settings
4. Start the blocking service using the FAB button

### Daily Usage
1. **Service Status**: Check if blocking service is active
2. **Quick Actions**: Use the floating action button to toggle service
3. **Statistics**: Monitor your progress in the Statistics tab
4. **Settings**: Adjust preferences as needed

### Blocking Methods
- **Redirect to Home**: Returns to home screen when content detected
- **Close App**: Closes the target app completely
- **Show Alert**: Displays a notification without closing the app

## How It Works

### Content Detection
The app uses Android's Usage Stats API to monitor which applications are currently active. When Instagram or YouTube is detected:

1. **App Launch Detection**: Service monitors app foreground events
2. **Content Analysis**: Analyzes app state for Reels/Shorts content
3. **Blocking Action**: Executes chosen blocking method
4. **Statistics Update**: Records the blocking event

### Service Architecture
- **Foreground Service**: Runs continuously to monitor app usage
- **Usage Stats Monitoring**: Checks active apps every second
- **Content Detection**: Uses accessibility features for content analysis
- **Blocking Execution**: Handles different blocking strategies

## Limitations & Considerations

### Current Limitations
- **Detection Accuracy**: Content detection relies on accessibility services
- **App Updates**: Changes to Instagram/YouTube may affect detection
- **Battery Usage**: Continuous monitoring may impact battery life
- **Permission Requirements**: Requires several system-level permissions

### Future Enhancements
- **Machine Learning**: Improve content detection accuracy
- **Custom Rules**: Allow users to define blocking patterns
- **Scheduling**: Time-based blocking rules
- **Whitelist**: Exclude specific content from blocking
- **Cloud Sync**: Backup settings and statistics

## Troubleshooting

### Common Issues

1. **Service Not Starting**
   - Check if usage access permission is granted
   - Ensure app is not battery optimized
   - Restart the app

2. **Content Not Being Blocked**
   - Verify Instagram/YouTube blocking is enabled in settings
   - Check if target apps are installed
   - Ensure service is running

3. **Permission Denied**
   - Go to device settings and manually grant permissions
   - Restart the app after granting permissions

### Performance Tips
- Keep the app updated
- Avoid excessive app switching
- Monitor battery usage
- Clear app cache if needed

## Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Follow Material Design guidelines

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, feature requests, or bug reports:
- Create an issue on GitHub
- Check the troubleshooting section
- Review the documentation

## Acknowledgments

- Material Design team for UI components
- Android developer community
- Open source contributors

---

**Note**: This app is designed for educational and productivity purposes. Users should respect platform terms of service and use responsibly.