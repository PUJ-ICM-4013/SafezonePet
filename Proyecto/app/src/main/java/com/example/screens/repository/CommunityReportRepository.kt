package com.example.screens.repository

import com.example.screens.data.CommunityReport
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CommunityReportRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val reports = firestore.collection("community_reports")

    suspend fun createReport(report: CommunityReport): CommunityReport {
        val doc = reports.document()
        val saved = report.copy(id = doc.id)
        doc.set(saved.toFirestoreMap()).await()
        return saved
    }

    suspend fun getRecentReports(limit: Long = 50): List<CommunityReport> {
        val snap = reports
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { d ->
            d.data?.let { CommunityReport.fromFirestore(d.id, it) }
        }
    }

    suspend fun getReport(reportId: String): CommunityReport? {
        val doc = reports.document(reportId).get().await()
        if (!doc.exists()) return null
        return CommunityReport.fromFirestore(doc.id, doc.data ?: emptyMap())
    }
}
