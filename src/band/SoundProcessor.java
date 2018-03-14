package band;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.Arrays;



public class SoundProcessor
{
  private static final double[] OCTAVE_FREQUENCIES = { 32.7032, 65.4064, 130.813, 261.626, 523.251, 1046.5, 2093.0, 4186.01 };

  public SoundProcessor()
  {
  }

  public MusicEvent processSound(float[] left, float[] right)
  {
    int length = left.length / 2;
    FloatFFT_1D fft = new FloatFFT_1D(length);
    fft.realForwardFull(left);
    fft.realForwardFull(right);

    double[] leftSpectrum = new double[left.length/2];
    double[] rightSpectrum = new double[right.length/2];
    double frequencyStep = 44100.0 / (double)length;

    for (int i=0; i<length/2; i++)
    {
      leftSpectrum[i] = Math.sqrt(left[i*2]*left[i*2]+left[i*2+1]*left[i*2+1]);
      rightSpectrum[i] = Math.sqrt(right[i*2]*right[i*2]+right[i*2+1]*right[i*2+1]);
    }

    return new MusicEvent(
            dominantFrequency(leftSpectrum, rightSpectrum, frequencyStep),
            amplitude(leftSpectrum, rightSpectrum, frequencyStep),
            octaveStrengths(leftSpectrum, rightSpectrum, frequencyStep),
            octavePeaks(leftSpectrum, rightSpectrum, frequencyStep));
  }

  private double dominantFrequency(double[] spectrumLeft, double[] spectrumRight, double frequencyStep)
  {
    int first = (int)(OCTAVE_FREQUENCIES[0] / frequencyStep) + 1;
    int last = (int)(OCTAVE_FREQUENCIES[OCTAVE_FREQUENCIES.length-1] / frequencyStep) + 1;
    int maxLeft = first;
    int maxRight = first;

    for (int i = first; i < last; i++)
    {
      maxLeft = spectrumLeft[i] > spectrumLeft[maxLeft] ? i : maxLeft;
      maxRight = spectrumRight[i] > spectrumRight[maxRight] ? i : maxRight;
    }
    return (double)(maxLeft + maxRight) * frequencyStep / 2.0;
  }

  private double amplitude(double[] spectrumLeft, double[] spectrumRight, double frequencySteps)
  {
    double sum = 0.0;
    int first = (int)(OCTAVE_FREQUENCIES[0] / frequencySteps) + 1;
    int last = (int)(OCTAVE_FREQUENCIES[OCTAVE_FREQUENCIES.length-1] / frequencySteps) + 1;

    for (int i = first; i < last; i++)
    {
      sum += (spectrumLeft[i] + spectrumRight[i]) / 2.0;
    }
    return sum / (double)(last-first);
  }

  private double[] octaveStrengths(double[] spectrumLeft, double[] spectrumRight, double frequencySteps)
  {
    int[] counts = new int[OCTAVE_FREQUENCIES.length-1];
    double[] octaveSums = new double[OCTAVE_FREQUENCIES.length-1];
    Arrays.fill(counts, 0);
    Arrays.fill(octaveSums, 0.0);

    int first = (int)(OCTAVE_FREQUENCIES[0] / frequencySteps) + 1;
    int last = (int)(OCTAVE_FREQUENCIES[OCTAVE_FREQUENCIES.length-1] / frequencySteps) + 1;

    int currentOctave = 0;
    for (int i = first; i < last; i++)
    {
      if (currentOctave < OCTAVE_FREQUENCIES.length-1 && i*frequencySteps > OCTAVE_FREQUENCIES[currentOctave+1])
      {
        currentOctave++;
      }
      octaveSums[currentOctave] += (spectrumLeft[i] + spectrumRight[i]) / 2.0;
      counts[currentOctave]++;
    }

    for (int i=0; i<octaveSums.length; i++)
    {
      octaveSums[i] = octaveSums[i] / (double)counts[i];
    }
    return octaveSums;
  }

  private double[] octavePeaks(double[] spectrumLeft, double[] spectrumRight, double frequencySteps)
  {
    double[] octaveMax = new double[OCTAVE_FREQUENCIES.length-1];
    Arrays.fill(octaveMax, 0.0);

    int first = (int)(OCTAVE_FREQUENCIES[0] / frequencySteps) + 1;
    int last = (int)(OCTAVE_FREQUENCIES[OCTAVE_FREQUENCIES.length-1] / frequencySteps) + 1;

    int currentOctave = 0;
    for (int i = first; i < last; i++)
    {
      if (currentOctave < OCTAVE_FREQUENCIES.length-1 && i*frequencySteps > OCTAVE_FREQUENCIES[currentOctave+1])
      {
        currentOctave++;
      }

      octaveMax[currentOctave] = Math.max((spectrumLeft[i] + spectrumRight[i]) / 2.0, octaveMax[currentOctave]);
    }

    return octaveMax;
  }

}
