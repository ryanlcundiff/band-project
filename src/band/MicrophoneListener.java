package band;

/**
 * Created by lloyd on 3/3/18.
 * (C) Artify Partners LLC
 */
public interface MicrophoneListener
{
  void onSoundHeard(float[] left, float[] right);
}
