# Block sprite generator

The generator resolves representative flat sprites from the vanilla Minecraft 26.2 client assets. It follows item models and inherited texture variables first, then falls back to deterministic blockstate models.

The matching client JAR must be installed at the standard launcher path:

```text
%APPDATA%/.minecraft/versions/26.2/26.2.jar
```

Generate the mapping and coverage report:

```shell
mvn -f tools/generator-pom.xml compile exec:java
```

Verify that checked-in generated files are current without changing them:

```shell
mvn -f tools/generator-pom.xml compile exec:java -Dgenerator.mode=--check
```

Generated files:

- `src/me/neoblade298/neocore/bukkit/util/vanilla-block-sprites.properties`
- `tools/vanilla-block-sprites-report.json`

Overrides in `BlockSpriteGenerator` select stable frames, recognizable faces, registered special atlases, and direct head objects backed by vanilla entity textures. Technical blocks without meaningful visuals are deliberately excluded.
