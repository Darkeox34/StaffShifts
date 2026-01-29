# Staff Time Tracking Plugin - Project Specification

## Overview
I am looking for a Minecraft plugin that helps me track my staff members' work hours automatically. I want to see exactly when they are online, how much of that time they were actually active, and give them a way to record what they did during their shift.

## Core Features

### 1. Automatic Shift Tracking
- **Automatic Start/End:** When a staff member joins the server, their "shift" should start automatically. When they leave, it should end.
- **Manual Overrides:** Sometimes staff might need to manually "clock in" or "clock out" if they are doing work that isn't automatically tracked.
- **Permission Based:** Only players with a specific "staff" permission should be tracked.

### 2. Active vs. Idle Time
- I want to know if they were actually working, not just standing around being AFK.
- **AFK Detection:** The plugin should detect if a staff member hasn't moved for a certain amount of time (configurable, e.g., 10 minutes).
- **Separate Clocks:** The plugin should track "Active Time" and "Idle Time" separately. If they are AFK, the time should count towards "Idle Time" instead of "Active Time".

### 3. Staff Dashboard (In-Game Menu)
Staff should have a command (e.g., `/stafftime`) that opens a clean menu with several options:
- **My Current Shift:** Shows how long they have been clocked in for the current session. (active/live time)
- **My History:** Shows a list of their last 5 shifts, including the dates, times, and notes.
- **Staff Notes:** A way for them to add a note to their current shift (e.g., `/stafftime addnote <message>`) to record what they accomplished.

### 4. Management Tools (Senior Staff Only)
Users with higher permissions should see extra options in the `/stafftime` menu or via commands:
- **Who's Working:** A list of all staff members currently clocked in.
- **Leaderboard:** A leaderboard showing the staff members who have put in the most active hours over the last week.
- **Manual Adjustments:** A command to add or remove "active" or "idle" time from a staff member's total, just in case they forgot to log out or had a technical issue.

## Technical Requirements
- **Reliability:** Data must be saved to a database so that if the server crashes, we don't lose the staff members' hours.
- **Configuration:** I should be able to easily change the AFK timeout duration, plugin messages, and the database connection details in a configuration file.
- **Clean UI:** I want the messages and menus to look modern and professional, using colors and clean formatting.