package band;

import javax.sound.sampled.*;





public class Microphone implements Runnable
{
  private final MicrophoneListener listener;
  private Thread listeningThread;
  private boolean signaled;

  public Microphone(MicrophoneListener listener)
  {
    this.listener = listener;
  }

  public synchronized void listen()
  {
    if (listeningThread == null)
    {
      signaled = false;
      listeningThread = new Thread(this);
      listeningThread.start();
    }
  }

  public synchronized void stopListening()
  {
    if (listeningThread != null)
    {
      signaled = true;
      try
      {
        listeningThread.join();
      }
      catch (InterruptedException e)
      {
        // Do nothing
      }
      listeningThread = null;
    }
  }

  private synchronized boolean isSignaled()
  {
    return signaled;
  }

  @Override
  public void run()
  {
    int frameSize = 4;
    int outputBufferSize = 4096;
    AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    TargetDataLine targetLine;
    try
    {
      targetLine = (TargetDataLine) AudioSystem.getLine(info);
      targetLine.open();
      targetLine.start();

      byte[] data = new byte[targetLine.getBufferSize() / 5];
      int readBytes;

      float[] leftBuffer = new float[outputBufferSize*2];
      float[] rightBuffer = new float[outputBufferSize*2];

      int outputBufferPosition = 0;
      while (!isSignaled())
      {
        readBytes = targetLine.read(data, 0, data.length);
        for (int i=0; i<=(readBytes-frameSize); i += frameSize)
        {
          leftBuffer[outputBufferPosition] = readSample(data[i], data[i+1]);
          rightBuffer[outputBufferPosition] = readSample(data[i+2], data[i+3]);
          outputBufferPosition++;
          if (outputBufferPosition == outputBufferSize)
          {
            listener.onSoundHeard(leftBuffer, rightBuffer);
            outputBufferPosition = 0;
          }
        }
      }

      targetLine.stop();
      targetLine.close();
    }
    catch (LineUnavailableException e)
    {
      throw new RuntimeException(e);
    }
  }

  public static float readSample(byte lowByte, byte highByte)
  {
    int sample = lowByte;
    sample |= highByte << 8;
    if ((highByte & 0x80) != 0)
    {
      sample |= 0xFFFF0000;
    }

    return ((float)sample) / 32768.0f;
  }
}
