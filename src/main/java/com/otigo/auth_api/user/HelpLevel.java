package com.otigo.auth_api.user;

/**
 * Velinin oyuna ne kadar yardım ettiğini belirten seviyeler.
 */
public enum HelpLevel {
    NONE,    // Hiç yardım etmedi
    LITTLE,  // Biraz yardım etti
    A_LOT,   // Çok yardım etti
    COMPLETED_BY_PARENT // Veli tamamladı
}