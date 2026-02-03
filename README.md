## Commands

The main command for the plugin is `/ss`.

### Staff Commands
*Permission: `staffshifts.staffer`*

| Command | Description |
| :--- | :--- |
| `/ss` | Base command. |
| `/ss shift` | Opens GUI. |
| `/ss help` | Displays the help menu with all available commands. |
| `/ss shift start` | Manually start a shift. |
| `/ss shift end` | Manually end the current shift. |
| `/ss shift addnote <message>` | Add a note to the current active shift. |
| `/ss shift listnotes` | List all notes added to the current shift. |
| `/ss shift removenote <index>` | Remove a specific note from the current shift. |

### Admin & Management Commands
*Permission: `staffshifts.management`*

| Command | Description |
| :--- | :--- |
| `/ss admin addtime <player> <active\|idle> <time>` | Add time to a player's last or current shift (e.g., `1h30m`). |
| `/ss admin removetime <player> <active\|idle> <time>` | Remove time from a player's last or current shift. |
| `/ss admin settime <player> <active\|idle> <time>` | Set the specific time for a player's shift. |
| `/ss testdata <staffers> <minShifts> <maxShifts>` | Generate random test data for performance testing. |
| `/ss testdata clear` | Clear all data from the database. |
| `/ss reload` | Reloads plugin config files. |

---

## Permissions

- `staffshifts.staffer`: Allows being tracked and using the staff dashboard.
- `staffshifts.management`: Allows access to administration tools, leaderboards, and time adjustments.

---

## Configuration

The plugin uses a `config.yml` for basic settings and `messages.yml` for full customization of in-game text.

### `config.yml`
```yaml
database:
  mysql:
    host: "localhost"
    port: 3306
    database: "staffshifts"
    username: "admin"
    password: "changethispassword"

# Time in seconds before a player is considered AFK
afk-threshold-seconds: 300
#Time in seconds for data autosave
autosave-seconds: 300
```
