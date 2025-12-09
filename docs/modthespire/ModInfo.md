# ModInfo

Mods can include a `ModTheSpire.json` file at the root of their .jar to provide metadata.

## ModTheSpire.json Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| modid | string | required | Unique identifier for the mod |
| name | string | required | User-friendly display name |
| description | string | "" | Mod description |
| version | string | "" | Mod version number |
| sts_version | string | "" | Specific Slay the Spire version supported |
| mts_version | string | "" | Minimum ModTheSpire version required |
| author_list | [string] | [] | List of mod creators |
| credits | string | "" | Acknowledgements or credits |
| dependencies | [string] | [] | Required mods (causes crash if missing) |
| optional_dependencies | [string] | [] | Optional mods (no crash if absent) |
| update_json | string | "" | URL for version updates (deprecated) |

## Example Configuration

```json
{
  "modid": "example",
  "name": "Example Mod",
  "author_list": ["kiooeht"],
  "description": "An example mod to be an example.",
  "version": "0.1",
  "sts_version": "03-29-2018",
  "mts_version": "2.6.0",
  "dependencies": ["basemod"]
}
```

## Versioning Requirements

Mod versions follow Semantic Versioning standards. Reference https://semver.org/ for proper formatting. Improper versioning prevents the auto-updater from functioning correctly.

## Legacy Format (Deprecated)

The older `ModTheSpire.config` format supported properties like name, author, description, and mts_version using simple key=value pairs. This format should no longer be used.
