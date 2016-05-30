# regexp
An experimental regular expression library.

## Syntax

| kinds of single-character expressions    | examples |
| ---------------------------------------- | -------- |
| any character, possibly including newline (s=true) | `.`      |
| character class                          | `[xyz]`  |
| negated character class                  | `[^xyz]` |
| Perl character class [(link)](https://github.com/google/re2/wiki/Syntax#perl) | `\d`     |
| negated Perl character class             | `\D`     |


|        | Composites              |
| ------ | ----------------------- |
| `xy`   | `x` followed by `y`     |
| `x\|y` | `x` or `y` (prefer `x`) |

|          | Repetions                      |
| -------- | ------------------------------ |
| `x*`     | zero or more `x`               |
| `x+`     | one or more `x`                |
| `x?`     | zero or one `x`                |
| `x{n,m}` | `n` or `n`+1 or ... or `m` `x` |
| `x{n,}`  | `n` or more `x`                |
| `x{n}`   | exactly `n` `x`                |

|      | Perl character classes (all ASCII-only) |
| ---- | --------------------------------------- |
| `\d` | digits (≡ `[0-9]`)                      |
| `\D` | not digits (≡ `[^0-9]`)                 |
| `\s` | whitespace (≡ `[\t\n\f\r ]`)            |
| `\S` | not whitespace (≡ `[^\t\n\f\r ]`)       |
| `\w` | word characters (≡ `[0-9A-Za-z_]`)      |
| `\W` | not word characters (≡ `[^0-9A-Za-z_]`) |

## Document

See [Wiki](https://github.com/ZhangZhenghao/regexp/wiki)
## Bibliography
- [Implementing Regular Expressions](https://swtch.com/~rsc/regexp/)
