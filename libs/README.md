# JARs locais de desenvolvimento

Os JARs abaixo são usados para compilar os adaptadores e iniciar o cliente de desenvolvimento com os backends instalados. O Gradle os declara como `compileOnly` e `localRuntime`; eles não são incluídos no JAR publicado do addon.

| Arquivo | Mod ID | Versão |
| --- | --- | --- |
| `sophisticatedcore-1.21.1-1.5.1.2341.jar` | `sophisticatedcore` | 1.5.1.2341 |
| `sophisticatedbackpacks-1.21.1-3.26.3.2158.jar` | `sophisticatedbackpacks` | 3.26.3.2158 |
| `appliedenergistics2-19.2.18.jar` | `ae2` | 19.2.18 |
| `refinedstorage-neoforge-2.0.9.jar` | `refinedstorage` | 2.0.9 |
| `toms_storage-1.21-2.4.2.jar` | `toms_storage` | 2.4.2 |

Esses arquivos locais não são versionados. Para reproduzir o ambiente, obtenha as mesmas versões compatíveis com Minecraft 1.21.1 e coloque-as nesta pasta com os nomes esperados pelo `build.gradle`.

O AE2 local também requer GuideME 21.1.1 ou superior. Instale uma versão NeoForge para Minecraft 1.21.1 em `runs/client/mods` para iniciar o cliente de desenvolvimento; esse mod não é empacotado no addon.

O ambiente de jogo também precisa das dependências exigidas por cada mod e de Sophisticated Backpacks/Core. O JAR de Tom’s declara Sophisticated Storage como dependência opcional; instale-o se for necessário para os cenários de integração escolhidos.
