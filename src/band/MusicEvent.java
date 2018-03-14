package band;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Created by lloyd on 3/12/18.
 * (C) Artify Partners LLC
 */
public class MusicEvent extends Event
{
  private final double dominantFrequency;
  private final double amplitude;
  private final double[] octaveStrengths;
  private final double[] octavePeaks;

  public static final EventType MUSIC_EVENT_TYPE = new EventType<>("MUSIC");

  public MusicEvent(double dominantFrequency, double amplitude, double[] octaveStrengths, double[] octavePeaks)
  {
    super(MUSIC_EVENT_TYPE);
    this.dominantFrequency = dominantFrequency;
    this.amplitude = amplitude;
    this.octaveStrengths = octaveStrengths;
    this.octavePeaks = octavePeaks;
  }

  public double getDominantFrequency()
  {
    return dominantFrequency;
  }

  public double getAmplitude()
  {
    return amplitude;
  }

  public double[] getOctaveStrengths()
  {
    return octaveStrengths;
  }

  public double[] getOctavePeaks()
  {
    return octavePeaks;
  }
}
