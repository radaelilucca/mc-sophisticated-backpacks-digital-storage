# Sophisticated Backpacks - Digital Storages Expansion

Este PRD registra o MVP original do projeto, antes da adoção do nome atual.

## PRD do MVP

### Objetivo

Adicionar upgrades ao Sophisticated Backpacks para transferir itens entre mochilas e sistemas de storage digital. O primeiro MVP valida o fluxo com **Applied Energistics 2 (AE2)**, **Refined Storage 2 (RS2)** e **Tom’s Simple Storage**.

Cada função terá seu próprio item universal. O jogador configura manualmente cada upgrade para um sistema e uma rede alvo. O MVP entrega uma única função: depósito manual do conteúdo da mochila por botão.

Plataforma inicial: Minecraft 1.21.1, NeoForge e Java 21. Sophisticated Backpacks e Sophisticated Core são dependências obrigatórias; AE2, RS2 e Tom’s são dependências opcionais.

### Experiência e requisitos funcionais

1. O jogador fabrica um upgrade universal de depósito remoto.
2. Com o upgrade na mão, usa **Shift + clique direito** em um endpoint compatível. O sistema clicado define o backend e o endpoint define a rede alvo.
3. O jogador instala o upgrade na mochila e configura os filtros na aba do upgrade.
4. Ao pressionar **Depositar**, os itens elegíveis da mochila são enviados à rede vinculada.

A aba mostra o sistema, o endpoint, a dimensão, o estado da conexão e os controles de filtro. Também oferece **Depositar** e **Desvincular**. A descrição do item informa como vincular uma rede e quais condições de conectividade ela exige.

- Cada mochila aceita no máximo um upgrade de depósito remoto. Não há busca automática ou failover para outra rede.
- Um novo vínculo válido substitui o anterior. Um clique em alvo incompatível ou indisponível mantém o vínculo atual.
- O botão move apenas itens do inventário da mochila aberta. Inventário do jogador, equipamento e slots de upgrades ficam fora da operação.
- O upgrade reutiliza filtros, aba e controles do depósito avançado do Sophisticated: lista de permissão, lista de bloqueio e comparação com os itens disponíveis no destino.
- Itens e componentes são preservados. Capacidade parcial transfere apenas a quantidade aceita e deixa o restante na mochila.
- Vínculo e filtros persistem ao retirar o upgrade, mover a mochila e reiniciar o mundo.

Receita inicial sugerida: shapeless, usando o upgrade de depósito avançado do Sophisticated, uma ender pearl e um diamante. A receita não exige itens de nenhum backend específico.

### Conectividade

| Backend | Endpoint para vincular | Requisitos para depositar |
| --- | --- | --- |
| AE2 | ME Wireless Access Point | O access point vinculado está ativo, conectado à rede operacional correta e cobre a posição atual da mochila. Respeitar o alcance calculado pelo AE2, inclusive boosters, energia e restrições de dimensão; não substituir esse cálculo por um raio próprio do addon. |
| Refined Storage 2 | Controller | A rede está operacional; validar a cobertura dos transmissores sem fio a partir da posição da mochila conforme o validador nativo do RS2, além da permissão de inserção do jogador. Não usar um alcance independente do addon. |
| Tom’s Simple Storage | Storage Terminal ou Crafting Terminal | A rede de inventário está conectada; para acesso sem fio, o jogador precisa carregar um Wireless Terminal do Tom’s. O adaptador usa o alcance do terminal encontrado (incluindo o alcance configurado do avançado), as opções do servidor e as regras de beacon. Sem um Wireless Terminal no inventário, permanece apenas a interação local do terminal, sem acesso remoto. O terminal de destino deve estar carregado. |

O upgrade não tem bateria própria nem define um raio de conexão próprio. Para cada operação, o backend é a autoridade sobre alcance, energia, cobertura, dimensão e permissões: AE2 usa o access point e seus boosters/regras de rede; RS2 usa seus transmissores e validador de jogador; Tom’s usa o Wireless Terminal carregado pelo jogador, seu alcance, as opções do servidor e os níveis de beacon. Sem o item sem fio do Tom’s, o alcance volta à interação local do terminal. Ampliações nativas, inclusive acesso entre dimensões quando disponíveis, são respeitadas; o addon não pode ampliar nem contornar essas regras. O destino precisa estar carregado quando exigido pelo backend, e o addon nunca carrega chunks.

A posição usada na validação é a do jogador para uma mochila carregada e a do bloco para uma mochila colocada. O jogador que pressiona **Depositar** é a identidade usada na checagem de permissão. Sem alcance, com rede offline, sem permissão ou sem backend instalado, a operação é recusada com mensagem clara; dados de vínculo são preservados para quando a dependência voltar.

### Diretrizes técnicas

**Requisito central:** usar o máximo possível das APIs e abstrações do Sophisticated. Reaproveitar o ciclo de vida de upgrades, armazenamento e filtros, o container e a aba de configuração, os controles de interface, a persistência e o contexto da mochila. Código próprio fica restrito ao vínculo, ao estado da conexão, aos adaptadores de storage e à transferência segura.

A arquitetura terá um núcleo comum e um adaptador para cada backend opcional. O contrato interno de cada adaptador precisa resolver o endpoint vinculado, validar conexão/cobertura/permissão, fornecer os itens do destino para o filtro por conteúdo e inserir itens informando a quantidade realmente aceita. Referências a classes opcionais ficam isoladas no adaptador correspondente.

Vínculos persistidos contêm somente identificadores serializáveis do backend e do endpoint. Objetos de mundo, rede, jogador e nós são resolvidos em tempo de execução. Não guardar objetos transitórios em dados da mochila.

Toda operação ocorre no servidor e revalida jogador, menu aberto, mochila, upgrade, destino, alcance e permissão ao receber o pedido. A transferência deve conferir o resultado real da inserção e devolver à mochila qualquer sobra. Não confiar apenas no valor simulado do backend. Cliques repetidos e pedidos desatualizados não podem duplicar ou perder itens.

### Fases do MVP

1. **Spike de viabilidade:** fixar versões compatíveis de Sophisticated, AE2, RS2 e Tom’s para Minecraft 1.21.1. Confirmar APIs de endpoints, alcance, permissões, consulta de itens e inserção parcial para cada backend. Registrar limitações antes da implementação compartilhada.
2. **Upgrade comum:** registrar o item universal, vínculo e desvínculo, estado, persistência, filtros Sophisticated, aba com botão e operação de depósito segura.
3. **Adaptadores:** ligar AE2, RS2 e Tom’s ao mesmo fluxo, validar ausência individual de dependências e rodar os cenários de integração.
4. **Entrega:** validar em jogo, documentar a infraestrutura exigida por backend e adicionar exemplos à documentação do projeto.

### Critérios de aceitação

- O jogo inicia com nenhum backend, com cada backend instalado isoladamente e com os três juntos.
- O upgrade universal se vincula, pode trocar de alvo, desvincular e manter a configuração após reinício.
- O upgrade universal funciona com mochila carregada e colocada no mundo, e os três backends são reconhecidos.
- Os três modos de filtro funcionam, incluindo distinção entre itens com componentes diferentes e correspondência com itens do destino.
- Alcance válido, saída de alcance e recuperação da conexão produzem os estados esperados.
- Em cada backend, os testes confirmam a fronteira nativa de alcance/cobertura: uma posição imediatamente dentro do limite permite a operação e uma imediatamente fora recusa; também validar upgrades de alcance/boosters e regras de dimensão/beacon quando suportados. No Tom’s, testar sem Wireless Terminal (somente interação local), com terminal básico/avançado e com os níveis de beacon configurados. Alterar apenas o alcance próprio do addon não pode conceder acesso.
- Rede sem energia, rede cheia, inserção parcial, destino removido, chunk descarregado e permissão negada não causam perda nem duplicação.
- Pedidos repetidos, desatualizados ou inválidos não executam transferências fora do servidor nem ignoram as checagens de permissão.
- O inventário de origem não é reinserido na própria rede por uma conexão externa, evitando ciclos e falsos relatos de transferência.
- Testes automatizados cobrem filtros, persistência, transferência parcial e validação dos pedidos. Testes em jogo confirmam alcance e interação para cada backend, inclusive em servidor dedicado.

Compilar com sucesso não substitui a validação em jogo dos três backends.

### Evolução após o MVP

O catálogo posterior parte das oito funções apresentadas pelo More Backpack Upgrades: depósito, coleta, magnetismo, alimentação, reposição, restock, bomba e alquimia. Cada função será um upgrade separado e universal, configurável para um dos sistemas compatíveis. Fluidos, automação, extração, crafting remoto e tiers avançados ficam fora deste MVP.

## Fontes

- [Sophisticated Backpacks: código 1.21.x](https://github.com/P3pp3rF1y/SophisticatedBackpacks/tree/1.21.x)
- [Sophisticated Core: código 1.21.x](https://github.com/P3pp3rF1y/SophisticatedCore/tree/1.21.x)
- [AE2: Wireless Access Point, guia 1.21.1](https://guide.appliedenergistics.org/1.21.1/items-blocks-machines/wireless_access_point)
- [Refined Storage: Wireless Transmitter](https://refinedmods.com/refined-storage/wireless-networking/wireless-transmitter.html)
- [RS2: validador público de posição de jogador](https://github.com/refinedmods/refinedstorage2/blob/support/2.x/refinedstorage-common-api/src/main/java/com/refinedmods/refinedstorage/common/api/support/network/item/NetworkItemPlayerValidator.java)
- [Tom’s Simple Storage: configuração NeoForge 1.21.1](https://github.com/tom5454/Toms-Storage/blob/1.21.1/NeoForge/src/main/java/com/tom/storagemod/Config.java)
- [Tom’s Simple Storage: validação do terminal 1.21.1](https://github.com/tom5454/Toms-Storage/blob/1.21.1/NeoForge/src/platform-shared/java/com/tom/storagemod/block/entity/StorageTerminalBlockEntity.java)
- [Tom’s Simple Storage: alcance do terminal sem fio avançado 1.21.1](https://github.com/tom5454/Toms-Storage/blob/1.21.1/NeoForge/src/platform-shared/java/com/tom/storagemod/item/AdvWirelessTerminalItem.java)
- [More Backpack Upgrades para NeoForge 1.21.1](https://www.curseforge.com/minecraft/mc-mods/more-backpack-upgrades-neoforge)
