# Magnet — plano de implementação

## 1. Objetivo e comportamento

Adicionar `remote_magnet_upgrade`. Procurar drops próximos à origem e encaminhá-los conforme prioridade Rede primeiro/Mochila primeiro. O padrão é Rede primeiro. Coletar somente itens; XP está fora desta versão.

Alcance de atração e conexão sem fio são independentes: um drop dentro da atração não concede acesso remoto. Usar alcance, limites e cooldowns do Magnet avançado nativo.

## 2. Premissas aplicáveis

[P03 — alcance](../UPGRADES.md#P03), [P04 — automação](../UPGRADES.md#P04), [P05 — prioridade](../UPGRADES.md#P05), [P06 — reaproveitamento](../UPGRADES.md#P06), [P08 — quantidades reais](../UPGRADES.md#P08), [P10 — ciclos](../UPGRADES.md#P10) e [P11 — desempenho](../UPGRADES.md#P11). Registro, vínculo e receita seguem [P02](../UPGRADES.md#P02) e [P07](../UPGRADES.md#P07).

## 3. Estado atual e dependências

**Implementação atual (2026-10-12):** Magnet reutiliza a faixa do item avançado nativo, bloqueadores do Sophisticated via accessor e transferência parcial com atualização do drop. Item, aba, prioridade e cooldown foram registrados; compilação e inicialização passaram. A textura `assets/sophisticated_backpacks_digital_storages_expansion/textures/item/remote_magnet_upgrade.png` parte do sprite avançado Sophisticated em 16×16. A paleta compartilhada usa verdes e roxos no símbolo, amarelo vivo nos cantos preenchidos, branco nos pontos verticais e azul diamante nos horizontais. Alcance, bloqueadores, aparência no inventário e coexistência com Pickup ainda aguardam validação em mundo.

**Verificado:** Core fornece `ITickableUpgrade`, `ContentsFilterLogic`, `MagnetUpgradeConfig.magnetRange` e `IMagnetPreventionChecker.isBlocked(Entity)`. O wrapper nativo registra checkers por `addMagnetPreventionChecker`; seu conjunto e rotinas de seleção são privados.

**Proposto:** serviço de varredura usando o roteamento do [Pickup](PICKUP.md), consulta ao registro real de bloqueadores por accessor localizado e contexto de tick validado. Não copiar esse registro em cache permanente.

## 4. Implementação

- Item/wrapper: implementar `ITickableUpgrade`, filtros avançados, prioridade e estado de cooldown por wrapper. Não implementar um segundo caminho de coleta normal via `IPickupResponseUpgrade`.
- Serviço: buscar `ItemEntity` na região nativa de atração; ordenar candidatos por distância e ID de entidade para processamento estável.
- Elegibilidade: respeitar entidade viva, stack presente, pickup delay, restrições de dono e impedimentos de magnetismo nativos. Não contornar proteção de outro mod.
- Accessor Mixin: leitura do conjunto nativo de `IMagnetPreventionChecker` na versão fixada; aplicar checkers à entidade/contexto conforme a semântica nativa. Não refletir campos por nome em runtime.
- Roteamento: oferecer cópia do stack atual, processar destinos na ordem configurada e atualizar entidade somente pela diferença aceita.
- Container/aba: controles de filtros e prioridade, estado; desvínculo somente pela receita de [P02](../UPGRADES.md#P02). Não mostrar opção de XP sem implementação.

## 5. Fluxo operacional

1. Receber callback server-side, validar jogador, wrapper e cooldown; sem jogador, pausar.
2. Resolver contexto e elegibilidade de atração; rede indisponível deixa somente o caminho local disponível.
3. Buscar candidatos dentro dos limites nativos e aplicar bloqueadores/filtro.
4. Para cada candidato ainda válido, simular e executar roteamento protegido.
5. Reduzir stack pela quantidade real aceita; remover entidade apenas se a sobra for vazia.
6. Reproduzir feedback nativo de coleta somente quando houver movimento e aplicar cooldown correspondente.

## 6. Falhas e ciclo de vida

Ambos os destinos cheios deixam o drop intacto. Não reservar entidades entre ticks. Outra mochila pode coletar primeiro; reler existência/stack imediatamente antes da transferência evita usar oferta antiga.

Mochila colocada sem jogador no callback não atrai itens. Sair de alcance da rede não altera o raio de atração e permite fallback local. Remover/desabilitar o upgrade interrompe buscas; guard e contexto não sobrevivem ao tick.

## 7. Etapas de entrega

- [ ] Integrar contexto e cooldown nativos; gate: nenhuma busca sem jogador ou fora da cadência.
- [ ] Integrar bloqueadores e elegibilidade; gate: drops protegidos não são modificados.
- [ ] Conectar roteamento e atualizar entidades; gate: transferências parciais conservam o resto.
- [ ] Registrar GUI/receita e regressão; gate: convivência com Pickup e múltiplas mochilas aprovada.

## 8. Validação e aceitação

Testes: fronteira de atração, ambas as prioridades, filtros, bloqueadores registrados, delay/dono, entidade removida, mudança do stack, destinos parciais/cheios e cooldown.

Em jogo: drops dentro/fora da atração e cobertura sem fio, backends isolados/juntos, Magnet com Pickup, duas mochilas próximas, mochila colocada sem jogador e XP no chão. **Aceito quando** somente drops elegíveis são coletados, XP não é processado e a quantidade remanescente nunca desaparece.

[Critérios comuns](../UPGRADES.md#P12) · [Voltar ao catálogo](../UPGRADES.md)
