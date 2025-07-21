from noise import pnoise2
import math

# Terrain and Noise Settings
CHUNK_SIZE = 16
SCALE = 150          # Tighter scale = more features per chunk
OCTAVES = 2           # Layers of noise
PERSISTENCE = 0.5        # Each layer weaker
LACUNARITY = 0.5      # Each layer more frequent
       # Optional: water level

def generate_chunks(x_range, z_range):
    chunk_dict = {}

    for chunk_x in range(x_range[0], x_range[1] + 1):
        for chunk_z in range(z_range[0], z_range[1] + 1):
            blocks = []

            base_x = chunk_x * CHUNK_SIZE
            base_z = chunk_z * CHUNK_SIZE

            for x in range(CHUNK_SIZE):
                for z in range(CHUNK_SIZE):
                    world_x = base_x + x
                    world_z = base_z + z

                    # Main base terrain
                    base_noise = pnoise2(
                        world_x / SCALE,
                        world_z / SCALE,
                        octaves=OCTAVES,
                        persistence=PERSISTENCE,
                        lacunarity=LACUNARITY,
                        base=0
                    )

                    # Get absolute value and apply cubic curve
                    val = 1+base_noise
                    height = int(val ** 2 * 64)

                    

                    blocks.append((world_x, height, world_z))

            chunk_dict[(chunk_x, chunk_z)] = blocks

    return chunk_dict
