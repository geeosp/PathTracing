# JavaCPUPathTracer

A Java-based CPU path tracer implementation using JavaFX for real-time visualization. This project renders 3D scenes using Monte Carlo path tracing algorithms.

## Project Overview

JavaCPUPathTracer is an educational path tracing engine that:
- Implements realistic lighting simulation using path tracing
- Supports multiple rendering algorithms (Path Tracing, Distance Rendering, Normal Rendering, Light Rendering)
- Uses multi-threaded rendering for improved performance
- Displays results in a JavaFX GUI window
- Supports OBJ file format for 3D models
- Includes material properties (diffuse, specular, transparency)

## Project Structure

```
src/main/java/geeosp/pathtracing/
├── Main.java                          # Application entry point
├── Renderer.java                       # Main rendering coordinator
├── Arquivo.java                        # File I/O utility
├── RenderBundle.java                   # Rendering bundle container
├── Algb.java                          # Algebra utilities
├── models/                             # 3D model definitions
│   ├── Model.java                     # Base model interface
│   ├── ObjModel.java                  # OBJ file loader
│   ├── SphereModel.java               # Sphere primitive
│   ├── QuadricModel.java              # Quadric surfaces
│   ├── ObjDifuseModel.java            # Diffuse OBJ models
│   ├── ObjLight.java                  # Light OBJ models
│   ├── Material.java                  # Material properties
│   └── Hit.java                        # Ray hit information
├── renderers/                          # Rendering algorithms
│   ├── RenderAlgorithm.java           # Algorithm interface
│   ├── PathTracingRenderer.java       # Path tracing implementation
│   ├── DistanceRenderer.java          # Distance-based rendering
│   ├── NormalRendererAlgorithm.java   # Normal visualization
│   └── LightRenderer.java             # Light-only rendering
└── scene/                              # Scene management
    ├── RenderScene.java               # Scene definition
    ├── Settings.java                  # Global configuration
    └── input/scene.txt                # Scene configuration file
```

## Prerequisites

- **Java 21 or higher** (JDK)
- **Gradle 9.0.0** or higher
- **JavaFX 21.0.2** (automatically managed by Gradle)

## Setup Instructions

### 1. Clone/Download the Project

Navigate to the project directory:
```bash
cd JavaCPUPathTracer
```

### 2. Configure the Scene (input/scene.txt)

The `input/scene.txt` file defines your 3D scene. Here's the format:

#### Basic Camera Settings
```
eye <x> <y> <z>                    # Camera position
size <width> <height>              # Output image resolution
ortho <left> <bottom> <right> <top> # Orthographic camera bounds
background <r> <g> <b>            # Background color (0.0-1.0)
ambient <intensity>                # Ambient light intensity
```

#### Rendering Configuration
```
nthreads <count>                   # Number of rendering threads
config <depth>                     # Ray tracing depth
npaths <samples>                   # Number of samples per pixel
seed <seed>                        # Random seed for reproducibility
tonemapping <exposure>             # Tone mapping exposure value
```

#### Objects and Lighting

**Spheres:**
```
sphere <cx> <cy> <cz> <radius> <r> <g> <b> <ka> <kd> <ks> <kt> <q> <n>
```
- `cx, cy, cz`: Center position
- `radius`: Sphere radius
- `r, g, b`: RGB color (0.0-1.0)
- `ka`: Ambient coefficient
- `kd`: Diffuse coefficient
- `ks`: Specular coefficient
- `kt`: Transparency coefficient
- `q`: Shininess exponent
- `n`: Refractive index

**OBJ Models:**
```
object <filename.obj> <r> <g> <b> <ka> <kd> <ks> <kt> <q> <n>
```
- Material properties same as spheres

**Lights:**
```
light <filename.obj> <r> <g> <b> <intensity>
```
- `filename.obj`: Light geometry file
- `r, g, b`: Light color
- `intensity`: Light brightness multiplier

**Base Node:**
```
basen <height>
```
- Creates a base/ground plane at specified height

#### Comments
Lines starting with `#` are ignored as comments.

### Example Scene Configuration

```
# Number of rendering threads
nthreads 12

# Camera settings
eye 0.0 0.0 5.7
size 200 200
ortho -1 -1 1 1
background 0.0 0.0 0
ambient 0.5

# Rendering configuration
config 8              # Max ray depth
npaths 32             # Samples per pixel
tonemapping 0.4       # Exposure
seed 9                # Random seed

# Add a light source
light light.obj 1.0 1.0 1.0 1.0

# Add objects
sphere 0.0 0.0 0.0 1 0.8 0.2 0.2 0.3 0.7 0 0 5 1
object wall.obj 1.0 1.0 1.0 0.3 0.7 0 0 5 1
```

## Building the Project

### Using Gradle

Build the project:
```bash
./gradlew build
```

On Windows:
```bash
gradlew.bat build
```

## Running the Application

### Run via Gradle

```bash
./gradlew run
```

On Windows:
```bash
gradlew.bat run
```

### Run from IDE

1. Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)
2. Right-click on `Main.java` and select "Run"
3. The application will start and display a JavaFX window with the rendered scene

## Material Properties Explanation

Each object has material properties that control how light interacts with it:

- **ka (Ambient Coefficient)**: How much ambient light contributes (0.0-1.0)
- **kd (Diffuse Coefficient)**: Lambertian diffuse reflection (0.0-1.0)
- **ks (Specular Coefficient)**: Mirror-like reflection (0.0-1.0)
- **kt (Transparency Coefficient)**: Transmission through material (0.0-1.0)
- **q (Shininess)**: Controls specular highlight size (higher = sharper)
- **n (Refractive Index)**: Bending of light through material (typically 1.0-2.0)

**Material Examples:**

Matte Surface:
```
sphere 0 0 0 1 1.0 1.0 1.0 0.3 0.7 0 0 5 1
```

Shiny Metal:
```
sphere 0 0 0 1 0.8 0.8 0.8 0 0.2 0.8 0 100 1
```

Transparent Glass:
```
sphere 0 0 0 1 1.0 1.0 1.0 0 0.1 0.2 0.8 5 1.5
```

## Rendering Algorithms

The project supports multiple rendering algorithms. In `Main.java`, you can enable different renderers:

```java
renderers.add(new PathTracingRenderer());           // Full path tracing
// renderers.add(new DistanceRenderer());            // Distance field visualization
// renderers.add(new NormalRendererAlgorithm());     // Surface normal visualization
// renderers.add(new LightRenderer());               // Light contribution only
```

## Performance Tips

1. **Reduce Resolution**: Lower `size` values render faster
2. **Reduce Samples**: Lower `npaths` values speed up rendering (less quality)
3. **Reduce Depth**: Lower `config` depth limits ray bounces
4. **Increase Threads**: Higher `nthreads` utilizes more CPU cores
5. **Use Simpler Geometry**: Fewer objects = faster rendering

## Output Files

- **dumb.txt**: Temporary file created during scene loading
- **output.ppm**: Scene rendered output file (if specified in config)

## Troubleshooting

### "JavaFX runtime components are missing"
- Ensure Java 21+ is installed
- Run via `./gradlew run` (uses correct module path)

### "input/scene.txt not found"
- Ensure the `input/` directory exists in the project root
- Verify `input/scene.txt` is properly formatted

### Slow Rendering
- Reduce image resolution, sample count, or ray depth
- Increase number of threads
- Simplify the scene

### Missing OBJ files
- Create or download OBJ files and place them in the project directory
- Reference them with correct relative paths in `scene.txt`

## Development

### Adding Custom Renderers

1. Create a new class extending `RenderAlgorithm`
2. Implement the required rendering logic
3. Add to the renderers list in `Main.java`

### Adding New Model Types

1. Create a class extending `Model`
2. Implement ray-intersection logic in the `hits()` method
3. Register in scene loader if needed

## License

See project for license information.

## Author

Original: Emannuel Macêdo (egm@cin.ufpe.br)  
Contributors: geeo

## References

- JavaFX Documentation: https://openjfx.io/
- Path Tracing: https://en.wikipedia.org/wiki/Path_tracing
- Wavefront OBJ Format: https://en.wikipedia.org/wiki/Wavefront_.obj_file
