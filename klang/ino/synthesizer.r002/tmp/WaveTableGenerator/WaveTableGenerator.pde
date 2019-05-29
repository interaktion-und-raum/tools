final int SAMPLES = 2048;
final int RANGE = 512;

for (int i=0; i <= SAMPLES; i++) {
  print(floor(sin((float)i / SAMPLES * 2 * PI) * RANGE) + ", ");
}