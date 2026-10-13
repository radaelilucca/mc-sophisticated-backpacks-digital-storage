# Refill — plano de implementação

## 1. Objetivo e comportamento

Adicionar `remote_refill_upgrade`. Repor automaticamente itens configurados nos slots do portador, extraindo da rede/mochila conforme prioridade. Padrão Rede primeiro. Preencher somente o déficit permitido pelo stack e slot, sem metas numéricas próprias.

Expor a seleção de destinos do Refill avançado nativo, incluindo os slots de mão suportados. Destino ocupado por identidade/componentes incompatíveis não é sobrescrito. Não atuar sobre jogadores próximos ou mochila colocada.

## 2. Premissas aplicáveis

[P04 — portador](../UPGRADES.md#P04), [P05 — fontes](../UPGRADES.md#P05), [P06 — interfaces nativas](../UPGRADES.md#P06), [P08 — extração](../UPGRADES.md#P08), [P10 — guard](../UPGRADES.md#P10) e [P11 — cooldown](../UPGRADES.md#P11). Aplicar [P02](../UPGRADES.md#P02), [P03](../UPGRADES.md#P03) e [P07](../UPGRADES.md#P07).

## 3. Estado atual e dependências

**Implementação atual (2026-10-12):** serviço por slot-alvo, filtro nativo, prioridade e callback de seleção de bloco estão registrados. A extração usa a camada compartilhada e tenta devolver itens não aceitos. Sua textura 16×16 parte do ícone avançado original e usa a paleta remota do Magnet; o modelo aponta para o asset local. Compilação e início do cliente passaram; aparência em jogo, destinos, seleção de bloco e recuperação exigem validação manual.

**Verificado:** Backpacks fornece `RefillUpgradeWrapper.TargetSlot`, seus codecs e controles de seleção. Refill nativo implementa `ITickableUpgrade`, `IFilteredUpgrade` e `IBlockPickResponseUpgrade`. Parte das rotinas de preenchimento é privada.

**GUI compacta (P02/P06):** a grade de seletores de destino fica à direita da grade nativa de itens, na mesma ordem de linhas e colunas. Os botões de 12 px têm 2 px de intervalo horizontal; cada modo mostra `A` para Any, `M` para mão principal, `O` para mão secundária ou `1`–`9` para a hotbar. Tooltips nativos e configurações persistidas são preservados; prioridade e vínculo ficam logo abaixo das grades.

**Proposto:** serviço de obtenção por identidade/quantidade sobre `ItemExtraction` e fonte local, reutilizando contratos e recuperação do [Restock](RESTOCK.md). `TargetSlot` é um tipo existente; o serviço remoto é novo. Preservar semântica pública das seleções, sem assumir acesso a métodos privados de preenchimento.

## 4. Implementação

- Wrapper: filtros avançados, mapa filtro → `TargetSlot`, prioridade e cooldown; salvar alterações via componentes/codificação nativa quando compatível.
- Serviço: calcular déficit pela identidade configurada e estado real do alvo; consultar/extrair somente o necessário. Processar regras em ordem de slot de filtro.
- Destino: usar API do inventário do jogador, validar aceitação e sincronizar alterações pelo caminho nativo. Não modificar equipamento fora dos destinos nativos expostos.
- Seleção de bloco: implementar `IBlockPickResponseUpgrade` usando o mesmo serviço; preservar regras nativas de slot selecionado e realocação da seleção anterior.
- Container/aba: adaptar controles nativos ao wrapper universal; expor seleção por filtro, prioridade e vínculo; desvínculo somente pela receita de [P02](../UPGRADES.md#P02).
- Item: limite um e conflito com Refill básico/avançado. Nenhum Mixin específico planejado.

## 5. Fluxo operacional

1. Receber tick do portador válido ou callback de seleção de bloco; conferir habilitação/cooldown aplicável.
2. Ler regras e destino atual; calcular déficit sem substituir item incompatível.
3. Planejar espaço e abrir guard de saída ao acessar rede que possa conter a própria mochila.
4. Extrair da fonte prioritária e da secundária apenas o déficit restante.
5. Revalidar destino e inserir o resultado real; recuperar qualquer excedente.
6. Atualizar inventário e cooldown. Na seleção de bloco, reportar sucesso somente quando o item estiver efetivamente disponível no destino nativo.

## 6. Falhas e ciclo de vida

Rede sem extração autorizada permite fonte local. Um slot modificado entre planejamento e commit não é sobrescrito; recuperar a oferta. Item não empilhável tem déficit máximo compatível com seu limite, não 64 fixo.

Não consumir reservas de Feeding/Alchemy e não retirar recursos de slots de upgrades. Remover a mochila ou trocar portador invalida a operação. Não manter rede ou inventário do jogador em campos persistidos.

## 7. Etapas de entrega

- [ ] Implementar obtenção por identidade e déficit; gate: quantidades extraídas nunca excedem espaço planejado.
- [ ] Adaptar configurações/controles de `TargetSlot`; gate: todas as seleções expostas persistem e funcionam.
- [ ] Implementar seleção de bloco; gate: inventário cheio e seleção anterior preservados conforme regras nativas.
- [ ] Registrar item e regressão; gate: nenhum abastecimento de jogador próximo ou mochila colocada.

## 8. Validação e aceitação

Testes: slots vazios/parciais/cheios, componentes distintos, stack máximo variável, mão principal/secundária, regra sobreposta, destino alterado, fonte parcial, recuperação e seleção de bloco sem espaço.

Em jogo: consumir blocos, tochas e foguetes, trocar o item do slot, selecionar bloco, perder cobertura e alternar prioridade nos três backends. **Aceito quando** somente o déficit chega ao portador, sem sobrescrita e com comportamento nativo de seleção de destino.

[Critérios comuns](../UPGRADES.md#P12) · [Voltar ao catálogo](../UPGRADES.md)
