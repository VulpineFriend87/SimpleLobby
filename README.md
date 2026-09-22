<a href="https://modrinth.com/plugin/simplelobby"><img alt="Available on Modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
<img alt="Works on Paper 1.18.2+" height="56" src="https://u.vulpine.top/u/AIB6AM.svg">
<img alt="Requires Java 17+" height="56" src="https://u.vulpine.top/u/ggP6Ta.svg">

SimpleLobby is a lightweight, flexible, and powerful plugin designed for easy lobby management and spawn-setting on your Minecraft server. Whether you need an authentication lobby, a world with custom join actions, or just a simple spawn point, SimpleLobby is the plugin you are looking for.

## Features

- **Easy Setup:** Plug-and-play; just drop it in your plugins folder and get started.
- **Lobby & Spawn Management:** Set a spawn location and teleport players with the `/spawn` command.
- **Customizable Actions:** Configure join/quit actions, teleport messages, titles, sounds, and more via `config.yml`.
- **Flexible Use Cases:** Perfect for authentication lobbies, minigame lobbies, world spawns, and events.
- **Permission System:** Fine-grained permission checks for commands and actions.
- **Placeholder Support:** Integrates with PlaceholderAPI for dynamic messages.
- **World Options:** Lock the lobby down as far as you need; hunger, damage, mob spawning, block breaking/placing/interaction, trampling, entities, items and inventories, fire and explosions — globally or per world.

## Why SimpleLobby?

- **Not just a lobby plugin:** Use it for authentication, world spawns, or any scenario where you need custom join actions or spawn management.
- **Custom actions:** Titles, messages, sounds, gamemodes, and more.
- **Lightweight:** Minimal overhead for fast performance.
- **Customization:** Every aspect is configurable to fit your server’s needs.

## Commands

| Command                  | Description                                                   |
|--------------------------|---------------------------------------------------------------|
| `/simplelobby`           | Main SimpleLobby command (shows plugin info, manages subcommands) |
| `/simplelobby reload`    | Reloads the plugin configuration                              |
| `/simplelobby setspawn`  | Sets the spawn location to your current position              |
| `/spawn`                 | Teleports the executor to the spawn (if enabled)              |

## Permissions

| Permission                      | Description                                                |
|----------------------------------|------------------------------------------------------------|
| `simplelobby.admin`              | Grants all SimpleLobby permissions                         |
| `simplelobby.command.spawn`      | Allows using the `/spawn` command                          |
| `simplelobby.command.reload`     | Allows using `/simplelobby reload`                         |
| `simplelobby.command.setspawn`   | Allows using `/simplelobby setspawn`                       |

Missing permissions will show a configurable "no permission" message.

## Requirements

- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) (optional for placeholder support)

## Support & Documentation

For more details, view the source code or open an issue on [GitHub](https://github.com/VulpineFriend87/SimpleLobby).

By <a href="https://vulpine.top">Vulpine</a>
