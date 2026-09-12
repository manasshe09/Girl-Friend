package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CouplePink
import com.example.ui.theme.TelegramCyan
import com.example.ui.theme.TelegramDarkInput
import com.example.ui.theme.TelegramDarkSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
  onSendMedia: (type: String, uri: String, fileName: String?, fileSize: String?, caption: String) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState()

  // Photo Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      onSendMedia("IMAGE", uri.toString(), "photo_${System.currentTimeMillis()}.jpg", "2.1 MB", "")
      onDismiss()
    }
  }

  // Document Picker
  val docPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri: Uri? ->
    if (uri != null) {
      onSendMedia("DOCUMENT", uri.toString(), "Document_${System.currentTimeMillis()}.pdf", "1.4 MB", "")
      onDismiss()
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = TelegramDarkSurface,
    modifier = Modifier.testTag("attachment_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Text(
        text = "Share with My Love",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 16.dp)
      )

      // 4 Main Options: Gallery / Photo, Video, Document, Romantic Note
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        AttachGridItem(
          icon = Icons.Default.Image,
          label = "Photo",
          gradient = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
          onClick = {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          }
        )

        AttachGridItem(
          icon = Icons.Default.Videocam,
          label = "Video",
          gradient = listOf(Color(0xFFF43F5E), Color(0xFFBE123C)),
          onClick = {
            onSendMedia(
              "VIDEO",
              "",
              "Romantic_Moments.mp4",
              "14.8 MB",
              "Our favorite memory video ❤️"
            )
            onDismiss()
          }
        )

        AttachGridItem(
          icon = Icons.Default.Description,
          label = "Document",
          gradient = listOf(Color(0xFF10B981), Color(0xFF047857)),
          onClick = {
            docPickerLauncher.launch(arrayOf("application/pdf", "application/msword", "text/plain"))
          }
        )

        AttachGridItem(
          icon = Icons.Default.Favorite,
          label = "Love Card",
          gradient = listOf(Color(0xFFEC4899), Color(0xFFA855F7)),
          onClick = {
            onSendMedia(
              "TEXT",
              "",
              null,
              null,
              "Sending you endless love and warm hugs! 🥰💖 You mean the world to me!"
            )
            onDismiss()
          }
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Couple Presets Section
      Text(
        text = "Couple Quick Presets",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF94A3B8),
        modifier = Modifier.padding(bottom = 10.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickPresetChip(
          title = "📄 Trip_Plan.pdf",
          subtitle = "2.4 MB",
          onClick = {
            onSendMedia("DOCUMENT", "", "Our_Next_Trip_Plan.pdf", "2.4 MB", "Can't wait for our trip! ✈️🏖️")
            onDismiss()
          },
          modifier = Modifier.weight(1f)
        )

        QuickPresetChip(
          title = "🎥 Love_Clip.mp4",
          subtitle = "8.2 MB",
          onClick = {
            onSendMedia("VIDEO", "", "Sunset_Together.mp4", "8.2 MB", "That sunset with you was magical 🌅")
            onDismiss()
          },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickPresetChip(
          title = "📸 Sunset_Date.jpg",
          subtitle = "3.1 MB",
          onClick = {
            onSendMedia("IMAGE", "", "Sunset_Date_Photo.jpg", "3.1 MB", "Look how cute we are together! 😍")
            onDismiss()
          },
          modifier = Modifier.weight(1f)
        )

        QuickPresetChip(
          title = "💌 Love_Letter.docx",
          subtitle = "540 KB",
          onClick = {
            onSendMedia("DOCUMENT", "", "Private_Love_Letter.docx", "540 KB", "Something written straight from the heart 💌")
            onDismiss()
          },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun AttachGridItem(
  icon: ImageVector,
  label: String,
  gradient: List<Color>,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .size(54.dp)
        .clip(CircleShape)
        .background(Brush.linearGradient(gradient)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = Color.White,
        modifier = Modifier.size(26.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
fun QuickPresetChip(
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = TelegramDarkInput,
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
      Text(
        text = title,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = subtitle,
        color = Color(0xFF94A3B8),
        fontSize = 10.sp
      )
    }
  }
}
