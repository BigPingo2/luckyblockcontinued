# Lucky Block — NeoForge 1.21.1 module

This folder is a **drop-in `neoforge/` module** for the Lucky Block repo. It targets **NeoForge 1.21.1** (Java 21).

## How to install into your repo
1) Copy the **`neoforge/`** folder into the repository root (next to `forge/` and `fabric/`).
2) Edit **`settings.gradle.kts`** and add:
   ```kotlin
   include(":neoforge")
   ```
3) In the **root `gradle.properties`**, add or update these keys:
   ```properties
   minecraft_version=1.21.1
   neo_version=21.1.148
   mod_version=13.1.0-neoforge-1.21.1
   ```
   You can change `neo_version` to any 21.1.x you prefer.

4) Build or run the client:
   ```bash
   ./gradlew :neoforge:build
   ./gradlew :neoforge:runClient
   ```

## Notes
- I copied **assets** and **data** from your 1.20.2 Forge jar and **moved the biome modifier** from `data/lucky/forge/biome_modifier/` to `data/lucky/neoforge/biome_modifier/`.
- The Kotlin `@Mod` entrypoint is minimal. Wire your registries/events here mirroring how the Forge module does it, but with the NeoForge packages and APIs.
- If your common code references `RegistryObject`, update to use `DeferredHolder` or a `Supplier<T>` field.

Files auto-copied from your jar:
- assets files: 31
- data files: 4
