# fStats API

This is the fStats API repository for the minecraft implementation.

---

## Localization

All translation files are located in the `src/main/resources/assets/fstats-api/lang/` folder.

### Adding a New Language

To contribute a new translation:

1. **Create a JSON file** inside `src/main/resources/assets/fstats-api/lang/` named with the language code, for example:

- `fr_fr` for French
- `de_de` for German
- `fi_fi` for Finnish  
  *(You can look up language codes (in-game variant) [here](https://minecraft.fandom.com/wiki/Language#Languages) if unsure)*

2. Use the lines from the `en_us.json` file as a reference.

3. **Run the minecraft instance locally** to test:
    ```bash
    # Client
    gradle runClient
    # or Server
    gradle runServer
    ```

> 📝 Tip: You don’t need to translate every file immediately — partial translations are welcome and helpful!

---

### Translation Progress

| Code | Language |        Status        |   Translator    |
|:----:|:--------:|:--------------------:|:---------------:|
|  en  | English  | Native (Always 100%) | Syorito Hatsuki |

---

Thanks for helping make fStats accessible to more people!
