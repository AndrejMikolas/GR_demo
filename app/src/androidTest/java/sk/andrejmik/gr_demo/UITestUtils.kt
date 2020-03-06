package sk.andrejmik.gr_demo

class UITestUtils
{
    companion object
    {
        /**
         * Safely waits for specified time in ms
         *
         * @param ms
         */
        fun sleep(ms: Int)
        {
            try
            {
                Thread.sleep(ms.toLong())
            } catch (e: InterruptedException)
            {
                e.printStackTrace()
            }
        }
    }
}