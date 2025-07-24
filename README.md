<p align="center">
  <img height="400" width="400" src="https://github.com/VulpineFriend87/SimpleLobby/blob/master/logo.png?raw=true" alt="SimpleLobby logo" />
</p>

<h1 align="center">SimpleLobby</h1>

<p align="center">
  <a href="https://www.spigotmc.org/">
    <img src="https://img.shields.io/badge/SpigotMC-f09630?style=for-the-badge&logo=spigotmc&logoColor=fff" alt="SpigotMC" />
  </a>
  <a href="https://modrinth.com/">
    <img src="https://img.shields.io/badge/Modrinth-%2300AF5C?style=for-the-badge&logo=modrinth&logoColor=fff" alt="Modrinth" />
  </a>
  <a href="https://github.com/VulpineFriend87/SimpleLobby/releases/latest">
    <img src="https://img.shields.io/github/v/release/VulpineFriend87/SimpleLobby?style=for-the-badge" alt="GitHub Latest Version" />
  </a>
</p>

<br><br>

SimpleLobby is a lightweight, flexible, and powerful plugin designed for easy lobby management and spawn-setting on your Minecraft server. Whether you need an authentication lobby, a world with custom join actions, or just a simple spawn point, SimpleLobby is the plugin you are looking for.

## Features

- **Easy Setup:** Plug-and-play; just drop it in your plugins folder and get started.
- **Lobby & Spawn Management:** Set a spawn location and teleport players with the `/spawn` command.
- **Customizable Actions:** Configure join/quit actions, teleport messages, titles, sounds, and more via `config.yml`.
- **Flexible Use Cases:** Perfect for authentication lobbies, minigame lobbies, world spawns, and events.
- **Permission System:** Fine-grained permission checks for commands and actions.
- **Placeholder Support:** Integrates with PlaceholderAPI for dynamic messages.
- **World Options:** Prevent hunger loss, damage, mob spawning, and block breaking/placing/interaction as needed.

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

- Minecraft server version 1.13 to 1.21
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) (optional for placeholder support)

## Support & Documentation

For more details, view the source code or open an issue on [GitHub](https://github.com/VulpineFriend87/SimpleLobby).

By <a href="https://vulpine.top">Vulpine</a>