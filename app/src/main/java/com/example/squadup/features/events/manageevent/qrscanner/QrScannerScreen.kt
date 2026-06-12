package com.example.squadup.features.events.manageevent.qrscanner

import androidx.compose.material3.MaterialTheme

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.squadup.core.ui.theme.SquadOrange
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.compose.ui.res.stringResource
import com.example.squadup.R

@Composable
fun QrScannerScreen(
    uiState: QrScannerUiState,
    onBackClick: () -> Unit,
    onQrDetected: (String) -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (hasCameraPermission &&
            uiState.result is QrVerificationResult.Scanning &&
            !uiState.isVerifying
        ) {
            val executor = remember { Executors.newSingleThreadExecutor() }
            val barcodeScanner = remember { BarcodeScanning.getClient() }
            var lastCode by remember { mutableStateOf("") }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(executor) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )
                                        barcodeScanner.process(image)
                                            .addOnSuccessListener { barcodes ->
                                                barcodes
                                                    .firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
                                                    ?.rawValue
                                                    ?.takeIf { it != lastCode && it.isNotBlank() }
                                                    ?.let { code ->
                                                        lastCode = code
                                                        onQrDetected(code)
                                                    }
                                            }
                                            .addOnCompleteListener { imageProxy.close() }
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }
                        runCatching {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )

            // Scanning hint at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 80.dp)
            ) {
                Text(
                    text = stringResource(R.string.qr_scanner_hint),
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }

        if (!hasCameraPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SquadOrange
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.qr_scanner_permission_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.qr_scanner_permission_desc),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = SquadOrange)
                    ) {
                        Text(stringResource(R.string.qr_scanner_grant))
                    }
                }
            }
        }

        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(12.dp)
                .statusBarsPadding()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.45f), CircleShape)
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = Color.White)
        }

        // Title bar
        Text(
            text = stringResource(R.string.qr_scanner_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 14.dp)
        )

        // Verifying overlay
        if (uiState.isVerifying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = SquadOrange)
                    Spacer(Modifier.height(14.dp))
                    Text("A verificar bilhete...", color = Color.White, fontSize = 14.sp)
                }
            }
        }

        // Result overlay
        AnimatedVisibility(
            visible = uiState.result !is QrVerificationResult.Scanning && !uiState.isVerifying,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ResultSheet(result = uiState.result, onReset = onReset)
        }
    }
}

@Composable
private fun ResultSheet(result: QrVerificationResult, onReset: () -> Unit) {
    val (bgColor, icon, title, subtitle) = when (result) {
        is QrVerificationResult.Valid -> ResultDisplay(
            bgColor = Color(0xFF1B5E20),
            icon = Icons.Outlined.CheckCircle,
            title = "Bilhete Válido",
            subtitle = "${result.playerName}\n${result.eventName}"
        )
        is QrVerificationResult.WrongEvent -> ResultDisplay(
            bgColor = Color(0xFFB71C1C),
            icon = Icons.Outlined.Cancel,
            title = "Bilhete Inválido",
            subtitle = "Este bilhete pertence a outro evento:\n${result.eventName}"
        )
        is QrVerificationResult.Expired -> ResultDisplay(
            bgColor = Color(0xFF4A148C),
            icon = Icons.Outlined.Info,
            title = "Evento Expirado",
            subtitle = "O evento já terminou:\n${result.eventName}"
        )
        is QrVerificationResult.NotFound -> ResultDisplay(
            bgColor = Color(0xFFB71C1C),
            icon = Icons.Outlined.Cancel,
            title = "Bilhete Não Encontrado",
            subtitle = "Este QR code não corresponde a nenhum bilhete válido."
        )
        is QrVerificationResult.Error -> ResultDisplay(
            bgColor = Color(0xFF37474F),
            icon = Icons.Outlined.Info,
            title = "Erro",
            subtitle = result.message
        )
        else -> return
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = bgColor,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                    contentColor = Color.White
                )
            ) {
                Text("Ler Outro Bilhete", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private data class ResultDisplay(
    val bgColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val subtitle: String
)

private operator fun ResultDisplay.component1() = bgColor
private operator fun ResultDisplay.component2() = icon
private operator fun ResultDisplay.component3() = title
private operator fun ResultDisplay.component4() = subtitle
