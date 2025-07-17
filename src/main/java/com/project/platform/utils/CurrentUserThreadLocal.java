package com.project.platform.utils;

import com.project.platform.dto.CurrentUserDTO;

public class CurrentUserThreadLocal {

    /**
     * Thread-local variable used to isolate user information in concurrent requests.
     */
    private static final ThreadLocal<CurrentUserDTO> CURRENT_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Clear user information
     */
    public static void clear() {
        CURRENT_USER_THREAD_LOCAL.remove();
    }

    /**
     * Store user information
     */
    public static void set(CurrentUserDTO currentUserDTO) {
        CURRENT_USER_THREAD_LOCAL.set(currentUserDTO);
    }

    /**
     * Retrieve current user information
     */
    public static CurrentUserDTO getCurrentUser() {
        return CURRENT_USER_THREAD_LOCAL.get();
    }
}
