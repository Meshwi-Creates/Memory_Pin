# 📌 Memory Pin

Memory Pin is an Android application for creatively preserving and organizing travel memories using photos, locations, captions, and stickers.

## 📖 Project Definition

Memory Pin is an Android application for creatively preserving travel memories using photos, locations, captions, and stickers.
It allows users to create, customize, save, and revisit their memories through a scrapbook-style interface.

## ✨ Features

- 📸 Add 1–3 photos to a memory
- 📍 Add a travel location
- ✍️ Add a short caption
- ✨ Add up to 8 stickers
- 🎨 Arrange photos, stickers, location, and caption freely
- 🔄 Resize and rotate photos
- ↕️ Move elements forward or backward
- 🗑️ Delete selected elements
- ✏️ Edit location and caption
- 💾 Save customized memories
- 🖼️ View all saved memories
- 🗺️ Explore memories through a travel map
- 👥 Explore memories through Places & People
- ❤️ Preview saved memories
- ✨ View similar memories based on location, date, or people

## 🎯 Objective

The main objective of Memory Pin is to provide a simple, creative, and interactive way to preserve travel experiences digitally instead of keeping memories scattered across a phone gallery.

## 🛠️ Technologies Used

- **Kotlin**
- **Android Studio**
- **XML**
- **ConstraintLayout**
- **FrameLayout**
- **SharedPreferences**
- **JSON**
- **Android Activity & Intent**
- **Git & GitHub**

## 📱 Application Modules

### 🏠 Home

The home screen provides access to:

- Latest memory
- Saved memories
- See Photos
- Memory Map
- Create New Memory

### ✏️ Create Memory

Users can create a new memory by adding:

- Photos
- Location
- Caption
- Stickers

### 🎨 Memory Canvas

The scrapbook canvas allows users to customize their memory.

Users can:

- Drag elements
- Resize photos
- Rotate photos
- Change element order
- Edit text
- Delete elements

### 🖼️ See Photos

Users can browse their saved memories and explore them through:

- All Memories
- Places & People

The Places & People section organizes memories using a hierarchy such as:

**Place → Subplace → People → Photos**

### 🗺️ Memory Map

The Memory Map provides a visual way to explore travel memories according to their locations.

### ❤️ Memory Preview

The preview screen displays the selected memory along with:

- Main memory image
- Location
- Caption
- Date
- Place
- People
- Sticker information
- Similar memories

## 💾 Data Storage

Memory Pin stores saved memory information locally on the device.

The application uses:

- **SharedPreferences** for storing memory information
- **JSON** for organizing memory data
- **Internal storage** for saved images and generated memory previews

## 🔄 Application Flow

```text
Home
  ↓
Create New Memory
  ↓
Add Photos
  ↓
Add Location & Caption
  ↓
Add Stickers
  ↓
Memory Canvas
  ↓
Arrange & Customize
  ↓
Save Memory
  ↓
Home / Gallery
  ↓
Preview / Explore Memories
