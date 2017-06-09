/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 BeeInstant
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.beeinstant.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Measure time with Timer such as latency of an API
 */
class Timer implements Metric {

    private final AtomicLong startTime = new AtomicLong(0);
    private final Recorder recorder = new Recorder(Unit.MILLI_SECOND);

    @Override
    public void startTimer() {
        this.startTime.set(System.currentTimeMillis());
    }

    @Override
    public void stopTimer() {
        final long startTimeValue = this.startTime.getAndSet(0);
        if (startTimeValue > 0) {
            final long duration = System.currentTimeMillis() - startTimeValue;
            this.recorder.record(duration, Unit.MILLI_SECOND);
        }
    }

    @Override
    public void incCounter(final int value) {
        throw new UnsupportedOperationException("Cannot increase a counter in a Timer");
    }

    @Override
    public void record(final double value, final Unit unit) {
        throw new UnsupportedOperationException("Cannot record measurements in a Counter");
    }

    @Override
    public String flushToString() {
        return this.recorder.flushToString();
    }

    @Override
    public Metric merge(final Metric newData) {
        if (newData instanceof Timer) {
            final Timer newTimer = (Timer) newData;
            this.startTime.set(newTimer.startTime.getAndSet(0));
            this.recorder.merge(newTimer.recorder);
            return this;
        }
        throw new RuntimeException("Merge with an invalid Timer");
    }
}
