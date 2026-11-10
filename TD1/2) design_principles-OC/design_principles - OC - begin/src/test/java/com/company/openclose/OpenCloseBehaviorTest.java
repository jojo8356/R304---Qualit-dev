package com.company.openclose;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

public class OpenCloseBehaviorTest {

    @Test
    public void callHistoryReturnsEmptyListForUnknownSubscriber() {
        Long subscriberId = System.nanoTime();

        List<CallHistory.Call> calls = CallHistory.getCurrentCalls(subscriberId);

        assertNotNull(calls);
        assertTrue(calls.isEmpty());
    }

    @Test
    public void callHistoryStoresCallsForSubscriber() {
        Long subscriberId = System.nanoTime();
        LocalDateTime firstCall = LocalDateTime.of(2024, 1, 15, 9, 0);
        LocalDateTime secondCall = LocalDateTime.of(2024, 1, 15, 9, 30);

        CallHistory.addSession(subscriberId, firstCall, 45);
        CallHistory.addSession(subscriberId, secondCall, 15);

        List<CallHistory.Call> calls = CallHistory.getCurrentCalls(subscriberId);
        assertEquals(2, calls.size());
        assertEquals(subscriberId, calls.get(0).getSubscriberId());
        assertEquals(45, calls.get(0).getDuration());
        assertEquals(15, calls.get(1).getDuration());
    }

    @Test
    public void internetSessionHistoryReturnsEmptyListForUnknownSubscriber() {
        Long subscriberId = System.nanoTime();

        List<InternetSessionHistory.InternetSession> sessions = InternetSessionHistory.getCurrentSessions(subscriberId);

        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
    }

    @Test
    public void internetSessionHistoryStoresSessionsForSubscriber() {
        Long subscriberId = System.nanoTime();
        LocalDateTime firstSession = LocalDateTime.of(2024, 2, 10, 8, 0);
        LocalDateTime secondSession = LocalDateTime.of(2024, 2, 10, 8, 15);

        InternetSessionHistory.addSession(subscriberId, firstSession, 150L);
        InternetSessionHistory.addSession(subscriberId, secondSession, 90L);

        List<InternetSessionHistory.InternetSession> sessions = InternetSessionHistory.getCurrentSessions(subscriberId);
        assertEquals(2, sessions.size());
        assertEquals(subscriberId, sessions.get(0).getSubscriberId());
        assertEquals(150L, sessions.get(0).getDataUsed());
        assertEquals(90L, sessions.get(1).getDataUsed());
    }

    @Test
    public void phoneSubscriberCalculatesBillUsingCallDurationAndRate() {
        Long subscriberId = System.nanoTime();
        PhoneSubscriber subscriber = new PhoneSubscriber();
        subscriber.setSubscriberId(subscriberId);
        subscriber.setBaseRate(20);

        CallHistory.addSession(subscriberId, LocalDateTime.of(2024, 3, 1, 10, 0), 50);
        CallHistory.addSession(subscriberId, LocalDateTime.of(2024, 3, 1, 11, 0), 30);

        assertEquals(16.0, subscriber.calculateBill(), 0.0001);
    }

    @Test
    public void phoneSubscriberFieldAccessorsRoundTripValues() {
        PhoneSubscriber subscriber = new PhoneSubscriber();
        subscriber.setSubscriberId(77L);
        subscriber.setAddress("123 Main St");
        subscriber.setPhoneNumber(5551234L);
        subscriber.setBaseRate(30);

        assertEquals(Long.valueOf(77L), subscriber.getSubscriberId());
        assertEquals("123 Main St", subscriber.getAddress());
        assertEquals(Long.valueOf(5551234L), subscriber.getPhoneNumber());
        assertEquals(30, subscriber.getBaseRate());
    }

    @Test
    public void ispSubscriberCalculatesBillAfterFreeUsageThreshold() {
        Long subscriberId = System.nanoTime();
        ISPSubscriber subscriber = new ISPSubscriber();
        subscriber.setSubscriberId(subscriberId);
        subscriber.setBaseRate(20);
        subscriber.setFreeUsage(150L);

        InternetSessionHistory.addSession(subscriberId, LocalDateTime.of(2024, 4, 2, 9, 0), 50L);
        InternetSessionHistory.addSession(subscriberId, LocalDateTime.of(2024, 4, 2, 11, 0), 120L);

        assertEquals(4.0, subscriber.calculateBill(), 0.0001);
    }

    @Test
    public void ispSubscriberFieldAccessorsRoundTripValues() {
        ISPSubscriber subscriber = new ISPSubscriber();
        subscriber.setSubscriberId(88L);
        subscriber.setAddress("456 Market St");
        subscriber.setPhoneNumber(5559876L);
        subscriber.setBaseRate(15);
        subscriber.setFreeUsage(75L);

        assertEquals(Long.valueOf(88L), subscriber.getSubscriberId());
        assertEquals("456 Market St", subscriber.getAddress());
        assertEquals(Long.valueOf(5559876L), subscriber.getPhoneNumber());
        assertEquals(15, subscriber.getBaseRate());
        assertEquals(75L, subscriber.getFreeUsage());
    }
}
