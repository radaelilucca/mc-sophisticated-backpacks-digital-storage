# JARs de auditoria local

Os JARs abaixo foram usados para a auditoria de APIs. Os builds resolvem dependências por versões imutáveis do Maven do Modrinth e Maven Central; nenhum desses arquivos locais é necessário para compilar ou é incluído no JAR publicado do addon.

| Arquivo | Mod ID | Versão |
| --- | --- | --- |
| `sophisticatedcore-1.21.1-1.5.1.2341.jar` | `sophisticatedcore` | 1.5.1.2341 |
| `sophisticatedbackpacks-1.21.1-3.26.3.2158.jar` | `sophisticatedbackpacks` | 3.26.3.2158 |
| `appliedenergistics2-19.2.18.jar` | `ae2` | 19.2.18 |
| `refinedstorage-neoforge-2.0.9.jar` | `refinedstorage` | 2.0.9 |
| `toms_storage-1.21-2.4.2.jar` | `toms_storage` | 2.4.2 |

Os arquivos locais não são versionados. As versões Maven usadas pelo NeoForge estão fixadas em `neoforge/build.gradle`.

GuideME 21.1.1 para NeoForge e 20.1.7 para Forge vêm do Maven Central e são dependências somente do ambiente de desenvolvimento. Esses mods não são empacotados no addon.

O ambiente de jogo também precisa das dependências exigidas por cada mod e de Sophisticated Backpacks/Core. O JAR de Tom’s declara Sophisticated Storage como dependência opcional; instale-o se for necessário para os cenários de integração escolhidos.

## Forge 1.20.1

Os JARs Forge usados na auditoria foram:

| Arquivo | Mod ID | Versão |
| --- | --- | --- |
| `sophisticatedcore-1.20.1-1.5.10.2402.jar` | `sophisticatedcore` | 1.5.10.2402 |
| `sophisticatedbackpacks-1.20.1-3.26.12.2206.jar` | `sophisticatedbackpacks` | 3.26.12.2206 |
| `appliedenergistics2-forge-15.4.11.jar` | `ae2` | 15.4.11 |
| `refinedstorage-1.12.4.jar` | `refinedstorage` | 1.12.4 |
| `toms_storage-1.20-1.7.1.jar` | `toms_storage` | 1.7.1 |

O módulo Forge fixa versões imutáveis desses arquivos pelo Maven do Modrinth; AE2 usa GuideME `20.1.7` do Maven Central. A compilação atual testa o pipeline de plataforma e o código comum, ainda sem referenciar as APIs Sophisticated/backend porque os wrappers e adaptadores Forge estão pendentes. Consulte [MULTILOADER.md](../docs/MULTILOADER.md).

## SCC 1.5.3 exception

Unlike the older audit-only artifacts above, ignored file scc-1.5.3.jar is required for NeoForge compilation. Obtain the supplied audited artifact and verify SHA-256 e81ed8d3fdab7ce9bc35efc58036b63dcb6baefd07613156599dd638f8e84d3c. The Gradle verifySccJar task enforces it. SCC is not packaged inside the addon. See ../docs/integrations/SCC.md for runtime profiles and limitations.
