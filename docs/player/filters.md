# Filters and source priority

Upgrade filters decide what can be handled. On filter types with a list mode, **Allow list** handles only matching entries and **Block list** excludes matching entries. Specialized tabs use the corresponding Sophisticated Backpacks controls: Restock uses Deposit filtering, Feeding accepts food, Alchemy accepts supported consumables, and Pump filters fluids.

Pickup and Magnet can match their contents filters against items stored in the backpack. This stored-item matching is independent of the source-priority control.

Upgrades that can use both the backpack and linked network offer two priorities:

- **Backpack first:** try the backpack as source or destination first, then the network for anything remaining.
- **Linked network first:** try the network first, then the backpack for anything remaining.

For example, Refill with Backpack first uses the requested item from the backpack before fetching the remainder from the network. Pickup with Linked network first tries the network as the destination before falling back to the backpack. Priority applies to Pickup, Magnet, Refill, Feeding, Alchemy, and Pump. Deposit always moves backpack items to the network; Restock always moves network items to the backpack.
