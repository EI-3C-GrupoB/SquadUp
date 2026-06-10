package com.example.squadup.core.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun TicketQrCard(
    codigoQr: String = "",
    modifier: Modifier = Modifier
) {
    val bitmap = remember(codigoQr) {
        if (codigoQr.isBlank()) return@remember null
        runCatching {
            val writer = QRCodeWriter()
            val matrix = writer.encode(codigoQr, BarcodeFormat.QR_CODE, 512, 512)
            val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
            for (x in 0 until 512) {
                for (y in 0 until 512) {
                    bmp.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bmp
        }.getOrNull()
    }

    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(175.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.QrCode2,
                    contentDescription = "QR Code",
                    tint = SquadTextPrimary,
                    modifier = Modifier.size(170.dp)
                )
            }
        }
    }
}
