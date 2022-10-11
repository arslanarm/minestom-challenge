# Minestom Challenge

## Tasks
- [x] Permission system
- [x] Basic commands:
  - [x] /stop
  - [x] /give \<item\> \[player\]
  - [x] /gamemode \<mode\>
  - [x] /kick \<player\> \[message\]
- [x] Pig with the carrot, including saddle

## Permission System

Permission system can be found in `me.plony.challenge.permissions`.
Main idea with the permission is to give developers DSL that can be used with
KStom's Kommand class. DSL is contained in `PermissionBuilder.kt` and looks like this:

```kotlin
Kommand { 
    permission(manager) { 
        name = "my.permission"
        condition { 
            checkPermission()
        }
  
        syntax {
            // ...
        }
       
        subcommand("something") { 
            subPermission { 
                name = "sub_permission" // my.permission.sub_permission
                syntax {
                    // ...
                }
            }
        }
        
  }
}
```


#### Tables and relation between them

Entities:
- Group
- User
- Permission

Relationships
- Group -> many-to-many -> Permission
- User -> many-to-many -> Permission
- Group -> many-to-many -> User : **Groups can have priority for user. Priority exists to resolve permission collision**
- Permission -> one-to-many -> Permission : **Parent and child**
- Group -> many-to-many -> Group : **Groups can inherit permissions of parent groups**

#### Commands
Users can manipulate permissions using `perm` command in `me.plony.challenge.commands.PermissionCommand.kt`

- `/perm` displays information about existing users and groups
- `/perm user <name>` - displays information about user's group and permissions
- `/perm user <name> permission add <permission>` - adds permission to user
- `/perm user <name> permission remove <permission>` - removes permission from user
- `/perm user <name> group add <groupName>` - adds user to group
- `/perm user <name> group remove <groupName>` - removes user from group
- `/perm create group <name>` - creates new group
- `/perm group <name>` - displays information about group and permissions
- `/perm group <name> parent` - displays information about group's parents
- `/perm group <name> parent add <parentName>` - adds group as a parent
- `/perm group <name> parent remove <parentName>` - removes parent group

## Basic commands

Yes. They are implemented. You can use MiniMessages in /kick.
Look into `me.plony.challenge.commands`

## Pig with the carrot

I implemented summoning pig using spawner egg, so you can just do:
`/give pig_egg_spawner` and `/give saddle`. All pigs have brain damage so,
they can only react to carrot on the stick. Shift to dismount can be buggy, 
sincerely don't know why

## Notes

I purposefully didn't implement:
- Configuration
- Configuration
- Configuration
- Configuration
- Configuration

Reasons why: 

### P.S.: Sorry for bad humor ;(