🌿 JavaFX Wildlife Ecosystem Simulation
An interactive, real-time ecosystem simulator demonstrating autonomous agent behavior, dynamic 2D physics, and clean software architecture.
This project is a feature-rich, interactive sandbox built with JavaFX. Watch as complex ecosystems unfold: predators hunt, herbivores graze, and aquatic life navigates procedurally handled terrain. Built with a strong emphasis on Object-Oriented Programming (OOP), this application serves as a comprehensive showcase of design patterns, intelligent pathfinding, and robust state management.

✨ Key Features

    Dynamic 2D Environment: A sprawling 32x32 grid-based map featuring distinct terrain types (Water, Dirt, Grass) that actively dictate entity behavior and movement capabilities.

    Interactive Camera System: Navigate the simulation seamlessly with mouse-drag panning and scroll-wheel zoom functionality.

    Procedural Seasons: Toggle dynamic weather cycles (Winter mode) that alter the visual landscape and ecosystem conditions on the fly.

    Autonomous Entities: Distinct AI behaviors for various species, ranging from passive grazing to predatory hunting, all operating independently within the simulation loop.

🏗️ OOP Architecture & Design Techniques

This project is engineered using strict Object-Oriented principles to ensure scalability, maintainability, and clean code.
1. Inheritance & Polymorphism

The ecosystem utilizes a deep inheritance hierarchy, starting from a foundational BaseEntity class.

    Hierarchy: BaseEntity ➡️ Animal / Plant ➡️ Specific Species (Deer, Wolf, Tiger, Human, Fish).

    Polymorphic Loop: By leveraging polymorphism, the simulation avoids messy type-checking. The core engine simply iterates through a unified collection of entities, executing their overridden behaviors via a clean global update and render loop.

2. Encapsulation & Clean State Management

Data integrity is maintained through strict encapsulation.

    State Control: Entity attributes (health, coordinates, status) are secured behind accessors and mutators.

    Configuration Management: A centralized Constants class manages global simulation variables (tile sizes, update intervals), preventing magic numbers.

    Vector Physics: Movement is abstracted into continuous dx and dy vector handling, allowing for smooth, decoupled entity physics.


💥 Collision & Movement Physics

The simulation implements custom 2D physics and collision detection to ensure entities interact realistically with their environment.

Advanced Water Constraints (Fish Class):
Instead of checking a single center-point, the Fish entity utilizes 4-corner bounding box validation. This ensures that every edge of the entity's sprite remains strictly within valid water tiles, completely preventing clipping onto land.

Furthermore, collision resolution processes the X and Y axes independently. When an aquatic entity strikes a shoreline, it does not rigidly bounce backward. Instead, the simulation halts movement only on the colliding axis, allowing the entity to seamlessly "slide" and glide along the complex contours of the shore.

🎮 How to Interact and Spawn Animals

Take full control of the simulation using the built-in InputController. You can adjust the flow of time, alter the camera, and manually populate the ecosystem.

UP/DOWN Arrow: Increase and Decrease the speed of the simulation.
R: Reset speed and everything to normal.
Spacebar: Pause the simulation.
V: Switch to BasicView - instead of sprites, Animals will be rendered as 2d shapes.
S: Switch between Summer and Winter.


Spawning System

Populating the map is driven by an intuitive click-to-place system.

    Press a Digit Key (0-9) to select your desired entity.

    Click anywhere on the map grid.

    The system will automatically calculate the raw coordinates, snap the entity to the nearest structural tile, and perform safety checks before placing it.

0: Rock (⚠️ Proximity Check: Cannot be placed within a 35px radius of an active animal.)
1: Grass (Can only be placed on a grass environment tile.)
2: Deer
3: Wolf
4: Rabbit
5: Fox 
6: Tiger
7: Human
8: Fish (🐟 Water Check: Must be placed directly on a water tile.)
9: CLEAR current selection.

GUI buttons are also provided - with a clear HUD to depict the current amount of each type of animal.