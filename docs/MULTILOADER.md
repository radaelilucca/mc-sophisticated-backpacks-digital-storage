# Base multiloader

## Implementação

O projeto entrega módulos Gradle `common`, `neoforge` e `forge`, com um JAR independente por loader. `common` compila em Java 17 e contém apenas código sem dependências de Minecraft. As APIs e classes Minecraft são compiladas por plataforma; não há um binário Minecraft compartilhado entre as duas versões.

O módulo `forge` contém os oito upgrades remotos: Deposit, Restock, Pickup, Magnet, Refill, Feeding, Alchemy e Pump. O código inclui registros, abas/configuração Sophisticated, persistência NBT, desvínculo, recursos 1.20.1, adaptadores de itens para AE2 15.x/Refined Storage 1.x/Tom's Storage 1.7.x e adaptadores de fluidos para AE2/Refined Storage. Tom's informa incompatibilidade de fluidos. Os adaptadores opcionais são carregados apenas quando o respectivo mod está instalado.

Regras e wrappers Minecraft foram portados para fontes Forge e NeoForge separadas. No estado atual, a única classe Java compartilhada em `common` é `TransferQuantities`; ampliar a extração de regras puras continua sendo uma melhoria arquitetural possível, sem mudar os contratos funcionais.

## Versões e build

| Módulo | Minecraft | Loader | Java |
| --- | --- | --- | --- |
| `forge` | 1.20.1 | Forge 47.4.10 | 17 |
| `neoforge` | 1.21.1 | NeoForge 21.1.236 | 21 |
| `common` | independente | — | 17 |

As dependências de desenvolvimento estão fixadas por artefatos Maven do Modrinth em `forge/build.gradle` e `neoforge/build.gradle`. O build Forge usa ModDevGradle Legacy Forge, gera refmap, inclui `MixinConfigs` e reobfusca o JAR com `reobfJar`. Os mods de storage não são incorporados ao artefato.

## Validação feita

| Verificação | Resultado | Limite da evidência |
| --- | --- | --- |
| `:common:test` | 3 testes aprovados | Contrato aritmético independente de Minecraft. |
| `:neoforge:test --rerun-tasks` | 51 testes aprovados | Suíte existente da linha 1.21.1. |
| `:forge:test` | 1 teste aprovado | Contrato de permissão INSERT/EXTRACT; não inicializa o jogo. |
| `:forge:build` | Aprovado | Compilação, JAR Forge reobfuscado, recursos, tag `tags/items`, oito receitas e configuração Mixin. |

O artefato gerado é `forge/build/libs/sophisticated_backpacks_digital_storages_expansion-forge-0.1.0.jar`. A inspeção do JAR confirmou as oito classes de item, receitas, tag `sophisticatedbackpacks:upgrade` e configuração Mixin. O ambiente de testes JUnit Forge não inicializa registries de Minecraft; por isso, o teste Forge atual permanece restrito a lógica pura.

## Validação ainda necessária

Não foram executados GameTests Forge, boot de cliente/servidor com o JAR final, nem cenários com redes reais AE2, Refined Storage e Tom's Storage. Permanecem pendentes a matriz de cinco perfis de backends, persistência em mundo, permissões/alcance, GUI sincronizada, troca de topologia e validação manual em cliente e servidor dedicado. Compilar e empacotar não comprova esses cenários.

As receitas e assets partem dos recursos 1.21.1. O build converte o campo `result.id` para `result.item`, move tags de `tags/item` para `tags/items`, e usa `pack_format: 15` no Forge. As diferenças de API de filtros, hooks, capabilities e NBT ficam no código específico de cada módulo.

Os contratos funcionais e limites de cada upgrade permanecem em [UPGRADES.md](UPGRADES.md) e nos [planos individuais](upgrades/).
