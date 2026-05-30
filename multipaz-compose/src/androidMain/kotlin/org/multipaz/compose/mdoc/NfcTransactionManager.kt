package org.multipaz.compose.mdoc

import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.multipaz.util.Logger

/**
 * A global manager to ensure only one NFC-triggered transaction is active at a time.
 */
object NfcTransactionManager {
    private const val TAG = "NfcTransactionManager"
    private val mutex = Mutex()
    private var activeTransactionJob: Job? = null

    suspend fun tryStartTransaction(startBlock: suspend () -> Job): Boolean {
        mutex.withLock {
            if (activeTransactionJob?.isActive == true) {
                Logger.i(TAG, "A transaction is already active. Ignoring new request.")
                return false
            }
            Logger.i(TAG, "Starting new unique transaction.")
            activeTransactionJob = startBlock()
            return true
        }
    }

    suspend fun cancelActiveTransaction() {
        mutex.withLock {
            activeTransactionJob?.cancel()
            activeTransactionJob = null
        }
    }
}
