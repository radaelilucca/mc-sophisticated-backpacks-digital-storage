# Pickup — plano de implementação

## 1. Objetivo e comportamento

Adicionar `remote_pickup_upgrade`. Ao caminhar sobre um drop, encaminhar itens filtrados para rede/mochila na prioridade configurada. Padrão Rede primeiro; a sobra final retorna ao fluxo original de coleta. Não coletar XP.

O upgrade responde à coleta normal, sem procurar entidades próximas. Respeitar quais mochilas o Sophisticated considera elegíveis e sua ordem de processamento.

## 2. Premissas aplicáveis

[P03 — infraestrutura](../UPGRADES.md#P03), [P04 — contexto](../UPGRADES.md#P04), [P05 — prioridade](../UPGRADES.md#P05), [P06 — instalação](../UPGRADES.md#P06), [P08 — simulação](../UPGRADES.md#P08) e [P10 — ciclos](../UPGRADES.md#P10). Seguir [P02](../UPGRADES.md#P02), [P07](../UPGRADES.md#P07) e [P11](../UPGRADES.md#P11).

## 3. Estado atual e dependências

**Implementação atual (2026-10-12):** wrapper, filtros, prioridade e serviço de roteamento estão registrados. `PickupContextMixin` usa MixinExtras para delimitar o jogador/evento durante o handler original do Sophisticated; inicialização do cliente passou. Sua textura 16×16 parte do ícone avançado original, incluindo o padrão de cantos e pontos de borda, e usa a paleta remota do Magnet; o modelo aponta para o asset local. Aparência em jogo, coleta, simulação, múltiplas mochilas e sobras ainda precisam de validação manual.

**Verificado:** `IPickupResponseUpgrade.pickup(Level, ItemStack, boolean)` não recebe jogador. `CommonEventHandler.onItemPickup(ItemEntityPickupEvent.Pre)` conhece o jogador e usa simulação antes da execução. `InventoryHelper` tem overloads com/sem jogador, mas o callback do upgrade permanece sem esse argumento.

**Proposto:** contexto transitório de pickup e roteamento compartilhado. Não obter autoridade de um tick anterior ou de GUI previamente aberta.

## 4. Implementação

- Wrapper: implementar `IPickupResponseUpgrade`, filtros avançados `ContentsFilterLogic` e prioridade persistida; negar atuação remota sem contexto válido.
- Mixin localizado: envolver o corpo original de `CommonEventHandler.onItemPickup` com escopo do jogador/evento, restaurando contexto anterior em `finally`. O escopo cobre simulação e execução e suporta aninhamento.
- Não substituir o handler original nem duplicar a iteração de mochilas. Conferir descritor exato no JAR; o Mixin é comum, sem referência a classes opcionais.
- Serviço: rotear uma cópia da oferta para destinos local/remoto, devolvendo o resto. Sobras do primeiro destino são a única oferta do segundo.
- Container/aba: filtros, prioridade, estado do vínculo; desvínculo somente pela receita de [P02](../UPGRADES.md#P02); sem botão de transferência.
- Item: conflito apenas com Pickup básico/avançado, mantendo convivência com Magnet.

## 5. Fluxo operacional

1. Entrar no escopo nativo de coleta com jogador e mundo válidos.
2. Resolver wrapper instalado/habilitado e origem carregada pelo jogador.
3. Verificar filtro; itens recusados retornam intactos.
4. Em simulação, estimar aceitação dos dois destinos sem mutação. Tom’s pode superestimar; isso não autoriza confirmar coleta antecipadamente.
5. Em execução, conectar novamente e inserir na ordem configurada, protegendo entrada externa na própria mochila durante acesso à rede.
6. Retornar sobra real ao Sophisticated, que conserva autoridade sobre atualização da entidade e conclusão da coleta.
7. Liberar escopo mesmo em exceção.

## 6. Falhas e ciclo de vida

Rede indisponível permite tentativa local; ambos cheios devolvem a oferta intacta. Falta de vínculo não concede acesso remoto. Não cancelar o evento adicionalmente nem remover a entidade por conta própria.

Simulação que estimou sucesso pode ser seguida de execução parcial: a sobra real precisa sobreviver. Troca de jogador, remoção do upgrade ou exceção nunca deixa contexto global acessível à próxima coleta. Configuração sobre apenas mochilas vestidas continua sendo a nativa.

## 7. Etapas de entrega

- [ ] Criar escopo e Mixin; gate: contexto correto em ambas as fases e limpeza garantida.
- [ ] Implementar roteamento/simulação; gate: zero mutações em simulação e conservação da oferta real.
- [ ] Registrar item e configuração; gate: conflitos corretos e persistência da prioridade/filtros.
- [ ] Validar fluxo nativo; gate: múltiplas mochilas e destinos parciais sem coleta duplicada.

## 8. Validação e aceitação

Testes: ambas as prioridades, filtro recusado, nenhuma conexão, destinos parciais/cheios, superestimativa do Tom’s, guard de entrada, contextos aninhados, limpeza em exceção e jogador diferente na coleta seguinte.

Em jogo: caminhar sobre drops nos três backends, slots nativos de mochila suportados, configurações de mochila vestida, múltiplas mochilas e Magnet junto. **Aceito quando** só a quantidade efetivamente aceita é encaminhada e o restante continua disponível ao fluxo original, sem depender da GUI.

[Critérios comuns](../UPGRADES.md#P12) · [Voltar ao catálogo](../UPGRADES.md)
