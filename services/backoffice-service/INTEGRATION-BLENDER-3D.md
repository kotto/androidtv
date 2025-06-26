# Intégration Blender 3D pour Incrustation Avatar Présentateur

## Vue d'ensemble

Ce guide explique comment intégrer l'image 2D du studio de nouvelles MaâtCore dans un environnement 3D Blender pour créer des incrustations d'avatars présentateurs réalistes.

## Prérequis

- Blender 3.6+ installé
- Image 2D du studio MaâtCore (fournie)
- Connaissances de base en Blender
- Plugin Blender pour l'intégration avec le backoffice (optionnel)

## Étapes d'intégration

### 1. Préparation de l'environnement Blender

#### Création du projet
```python
# Script Python pour Blender
import bpy
import bmesh
from mathutils import Vector

# Nettoyer la scène par défaut
bpy.ops.object.select_all(action='SELECT')
bpy.ops.object.delete(use_global=False)

# Configurer les unités
bpy.context.scene.unit_settings.system = 'METRIC'
bpy.context.scene.unit_settings.scale_length = 1.0
```

#### Configuration de la caméra
```python
# Ajouter une caméra pour la vue présentateur
bpy.ops.object.camera_add(location=(0, -8, 1.8))
camera = bpy.context.active_object
camera.name = "Camera_Presenter"

# Configurer les paramètres de la caméra
camera.data.lens = 35  # Focale 35mm pour un rendu naturel
camera.data.sensor_width = 36
camera.rotation_euler = (1.1, 0, 0)  # Légère inclinaison vers le bas

# Définir comme caméra active
bpy.context.scene.camera = camera
```

### 2. Import et configuration de l'image 2D

#### Création du plan de fond
```python
# Créer un plan pour l'arrière-plan
bpy.ops.mesh.primitive_plane_add(size=20, location=(0, 5, 0))
background_plane = bpy.context.active_object
background_plane.name = "Studio_Background"

# Rotation pour faire face à la caméra
background_plane.rotation_euler = (1.5708, 0, 0)  # 90 degrés en X
```

#### Application de l'image comme texture
```python
# Créer un matériau pour l'arrière-plan
mat_background = bpy.data.materials.new(name="Studio_Material")
mat_background.use_nodes = True
background_plane.data.materials.append(mat_background)

# Configurer les nodes du matériau
nodes = mat_background.node_tree.nodes
links = mat_background.node_tree.links

# Supprimer le node par défaut
nodes.clear()

# Ajouter les nodes nécessaires
output_node = nodes.new(type='ShaderNodeOutputMaterial')
bsdf_node = nodes.new(type='ShaderNodeBsdfPrincipled')
image_node = nodes.new(type='ShaderNodeTexImage')

# Charger l'image du studio
# Remplacer par le chemin vers votre image
studio_image = bpy.data.images.load("/path/to/studio_maatcore.jpg")
image_node.image = studio_image

# Connecter les nodes
links.new(image_node.outputs['Color'], bsdf_node.inputs['Base Color'])
links.new(bsdf_node.outputs['BSDF'], output_node.inputs['Surface'])

# Désactiver la réflexion pour un rendu mat
bsdf_node.inputs['Roughness'].default_value = 1.0
bsdf_node.inputs['Specular'].default_value = 0.0
```

### 3. Création de l'espace présentateur

#### Zone de positionnement avatar
```python
# Créer un plan invisible pour le positionnement de l'avatar
bpy.ops.mesh.primitive_plane_add(size=2, location=(0, 0, 0))
avatar_zone = bpy.context.active_object
avatar_zone.name = "Avatar_Zone"

# Rendre invisible en rendu mais visible en viewport
avatar_zone.hide_render = True

# Matériau wireframe pour la visualisation
mat_zone = bpy.data.materials.new(name="Avatar_Zone_Material")
mat_zone.use_nodes = True
avatar_zone.data.materials.append(mat_zone)

# Configuration wireframe
nodes = mat_zone.node_tree.nodes
nodes.clear()
output = nodes.new(type='ShaderNodeOutputMaterial')
emission = nodes.new(type='ShaderNodeEmission')
emission.inputs['Color'].default_value = (0, 1, 0, 1)  # Vert
emission.inputs['Strength'].default_value = 2.0
mat_zone.node_tree.links.new(emission.outputs['Emission'], output.inputs['Surface'])
```

#### Éclairage du studio
```python
# Éclairage principal (Key Light)
bpy.ops.object.light_add(type='AREA', location=(-3, -2, 3))
key_light = bpy.context.active_object
key_light.name = "Key_Light"
key_light.data.energy = 100
key_light.data.size = 2
key_light.data.color = (1, 0.95, 0.8)  # Légèrement chaud

# Éclairage de remplissage (Fill Light)
bpy.ops.object.light_add(type='AREA', location=(3, -2, 2))
fill_light = bpy.context.active_object
fill_light.name = "Fill_Light"
fill_light.data.energy = 50
fill_light.data.size = 3
fill_light.data.color = (0.8, 0.9, 1)  # Légèrement froid

# Éclairage d'arrière-plan (Rim Light)
bpy.ops.object.light_add(type='SPOT', location=(0, 2, 2))
rim_light = bpy.context.active_object
rim_light.name = "Rim_Light"
rim_light.data.energy = 80
rim_light.data.spot_size = 1.2
rim_light.rotation_euler = (2.8, 0, 3.14159)
```

### 4. Configuration du rendu

#### Paramètres de rendu pour l'incrustation
```python
# Configuration du moteur de rendu
bpy.context.scene.render.engine = 'CYCLES'
bpy.context.scene.cycles.samples = 128
bpy.context.scene.cycles.use_denoising = True

# Résolution pour la diffusion
bpy.context.scene.render.resolution_x = 1920
bpy.context.scene.render.resolution_y = 1080
bpy.context.scene.render.resolution_percentage = 100

# Format de sortie
bpy.context.scene.render.image_settings.file_format = 'PNG'
bpy.context.scene.render.image_settings.color_mode = 'RGBA'
bpy.context.scene.render.film_transparent = True
```

#### Configuration pour l'alpha (transparence)
```python
# Activer la transparence pour l'incrustation
bpy.context.scene.render.film_transparent = True
bpy.context.scene.view_layers["ViewLayer"].use_pass_alpha = True

# Configuration du compositor pour l'alpha
bpy.context.scene.use_nodes = True
tree = bpy.context.scene.node_tree

# Nettoyer les nodes existants
for node in tree.nodes:
    tree.nodes.remove(node)

# Ajouter les nodes nécessaires
render_layers = tree.nodes.new(type='CompositorNodeRLayers')
composite = tree.nodes.new(type='CompositorNodeComposite')
alpha_over = tree.nodes.new(type='CompositorNodeAlphaOver')

# Connecter les nodes
tree.links.new(render_layers.outputs['Image'], composite.inputs['Image'])
tree.links.new(render_layers.outputs['Alpha'], composite.inputs['Alpha'])
```

### 5. Import et positionnement de l'avatar

#### Script pour l'import d'avatar
```python
def import_avatar(avatar_path, position=(0, 0, 0)):
    """
    Importe un avatar et le positionne dans la zone dédiée
    """
    # Import du fichier avatar (FBX, OBJ, etc.)
    if avatar_path.endswith('.fbx'):
        bpy.ops.import_scene.fbx(filepath=avatar_path)
    elif avatar_path.endswith('.obj'):
        bpy.ops.import_scene.obj(filepath=avatar_path)
    
    # Sélectionner l'avatar importé
    avatar = bpy.context.selected_objects[0]
    avatar.name = "Avatar_Presenter"
    
    # Positionner l'avatar
    avatar.location = position
    avatar.scale = (1, 1, 1)  # Ajuster selon les besoins
    
    return avatar

# Exemple d'utilisation
# avatar = import_avatar("/path/to/avatar.fbx", (0, 0, 0))
```

#### Animation et rigging
```python
def setup_avatar_animation(avatar):
    """
    Configure l'animation de base pour l'avatar présentateur
    """
    # Ajouter un armature si nécessaire
    bpy.context.view_layer.objects.active = avatar
    bpy.ops.object.mode_set(mode='OBJECT')
    
    # Animation de respiration subtile
    avatar.animation_data_create()
    action = bpy.data.actions.new(name="Breathing")
    avatar.animation_data.action = action
    
    # Keyframes pour la respiration
    fcurve = action.fcurves.new(data_path="scale", index=2)  # Scale Z
    fcurve.keyframe_points.add(2)
    fcurve.keyframe_points[0].co = (1, 1.0)
    fcurve.keyframe_points[1].co = (60, 1.02)  # Légère expansion
    
    # Interpolation fluide
    for keyframe in fcurve.keyframe_points:
        keyframe.interpolation = 'BEZIER'
        keyframe.handle_left_type = 'AUTO'
        keyframe.handle_right_type = 'AUTO'
```

### 6. Intégration avec le système TTS/HeyGen

#### Script de synchronisation
```python
import json
import requests
from typing import Dict, Any

class BlenderMaatCoreIntegration:
    def __init__(self, backoffice_url: str, api_key: str):
        self.backoffice_url = backoffice_url
        self.api_key = api_key
        self.headers = {
            'Authorization': f'Bearer {api_key}',
            'Content-Type': 'application/json'
        }
    
    def get_broadcast_data(self, broadcast_id: str) -> Dict[str, Any]:
        """
        Récupère les données de diffusion depuis le backoffice
        """
        response = requests.get(
            f"{self.backoffice_url}/api/news/broadcasts/{broadcast_id}",
            headers=self.headers
        )
        return response.json()
    
    def sync_avatar_animation(self, broadcast_id: str):
        """
        Synchronise l'animation de l'avatar avec les données audio/vidéo
        """
        broadcast_data = self.get_broadcast_data(broadcast_id)
        
        if 'audioUrl' in broadcast_data:
            # Importer l'audio pour la synchronisation labiale
            audio_path = self.download_audio(broadcast_data['audioUrl'])
            self.setup_lip_sync(audio_path)
        
        if 'duration' in broadcast_data:
            # Ajuster la durée de l'animation
            self.set_animation_duration(broadcast_data['duration'])
    
    def download_audio(self, audio_url: str) -> str:
        """
        Télécharge le fichier audio pour la synchronisation
        """
        response = requests.get(audio_url)
        audio_path = "/tmp/broadcast_audio.mp3"
        
        with open(audio_path, 'wb') as f:
            f.write(response.content)
        
        return audio_path
    
    def setup_lip_sync(self, audio_path: str):
        """
        Configure la synchronisation labiale avec l'audio
        """
        # Utiliser un addon comme Rhubarb Lip Sync ou JALI
        # Ceci nécessite des addons spécialisés
        pass
    
    def render_broadcast(self, output_path: str):
        """
        Lance le rendu de la diffusion
        """
        bpy.context.scene.render.filepath = output_path
        bpy.ops.render.render(animation=True)

# Utilisation
# integration = BlenderMaatCoreIntegration(
#     "http://localhost:3000",
#     "your-api-key"
# )
# integration.sync_avatar_animation("broadcast-id")
```

### 7. Automatisation et pipeline

#### Script de rendu automatique
```python
import os
import sys
import subprocess
from pathlib import Path

def automated_render_pipeline(broadcast_id: str, output_dir: str):
    """
    Pipeline automatisé pour le rendu des diffusions
    """
    # Chemins
    blender_exe = "blender"  # ou chemin complet vers Blender
    blend_file = "studio_maatcore.blend"
    script_file = "render_broadcast.py"
    
    # Commande Blender en mode headless
    cmd = [
        blender_exe,
        blend_file,
        "--background",
        "--python", script_file,
        "--",  # Arguments pour le script Python
        broadcast_id,
        output_dir
    ]
    
    # Exécution
    try:
        result = subprocess.run(cmd, capture_output=True, text=True)
        if result.returncode == 0:
            print(f"Rendu terminé: {output_dir}")
            return True
        else:
            print(f"Erreur de rendu: {result.stderr}")
            return False
    except Exception as e:
        print(f"Erreur d'exécution: {e}")
        return False

def batch_render_queue():
    """
    Traite une queue de rendus en attente
    """
    # Récupérer la liste des diffusions en attente
    # depuis le backoffice
    pending_broadcasts = get_pending_broadcasts()
    
    for broadcast in pending_broadcasts:
        output_path = f"/renders/{broadcast['id']}"
        success = automated_render_pipeline(broadcast['id'], output_path)
        
        if success:
            # Mettre à jour le statut dans le backoffice
            update_broadcast_status(broadcast['id'], 'RENDERED')
        else:
            update_broadcast_status(broadcast['id'], 'RENDER_FAILED')
```

### 8. Configuration des matériaux avancés

#### Matériau pour l'incrustation parfaite
```python
def create_chroma_key_material():
    """
    Crée un matériau pour l'incrustation chroma key
    """
    mat = bpy.data.materials.new(name="ChromaKey_Material")
    mat.use_nodes = True
    nodes = mat.node_tree.nodes
    links = mat.node_tree.links
    
    # Nettoyer les nodes par défaut
    nodes.clear()
    
    # Nodes principaux
    output = nodes.new(type='ShaderNodeOutputMaterial')
    mix_shader = nodes.new(type='ShaderNodeMixShader')
    transparent = nodes.new(type='ShaderNodeBsdfTransparent')
    principled = nodes.new(type='ShaderNodeBsdfPrincipled')
    
    # Node pour la détection de couleur
    color_ramp = nodes.new(type='ShaderNodeValToRGB')
    separate_rgb = nodes.new(type='ShaderNodeSeparateRGB')
    image_texture = nodes.new(type='ShaderNodeTexImage')
    
    # Configuration du color ramp pour le chroma key
    color_ramp.color_ramp.elements[0].position = 0.4
    color_ramp.color_ramp.elements[1].position = 0.6
    
    # Connexions
    links.new(image_texture.outputs['Color'], separate_rgb.inputs['Image'])
    links.new(separate_rgb.outputs['G'], color_ramp.inputs['Fac'])  # Canal vert
    links.new(color_ramp.outputs['Fac'], mix_shader.inputs['Fac'])
    links.new(transparent.outputs['BSDF'], mix_shader.inputs[1])
    links.new(principled.outputs['BSDF'], mix_shader.inputs[2])
    links.new(mix_shader.outputs['Shader'], output.inputs['Surface'])
    
    return mat
```

### 9. Export et intégration finale

#### Configuration d'export pour le streaming
```python
def setup_streaming_export():
    """
    Configure l'export pour le streaming en temps réel
    """
    scene = bpy.context.scene
    
    # Format optimisé pour le streaming
    scene.render.image_settings.file_format = 'FFMPEG'
    scene.render.ffmpeg.format = 'MPEG4'
    scene.render.ffmpeg.codec = 'H264'
    
    # Paramètres de qualité
    scene.render.ffmpeg.constant_rate_factor = 'HIGH'
    scene.render.ffmpeg.ffmpeg_preset = 'FAST'
    
    # Résolution et framerate
    scene.render.fps = 30
    scene.render.resolution_x = 1920
    scene.render.resolution_y = 1080
    
    # Audio
    scene.render.ffmpeg.audio_codec = 'AAC'
    scene.render.ffmpeg.audio_bitrate = 192

def export_for_web():
    """
    Export optimisé pour la diffusion web
    """
    # Rendu en WebM pour une meilleure compatibilité web
    bpy.context.scene.render.image_settings.file_format = 'FFMPEG'
    bpy.context.scene.render.ffmpeg.format = 'WEBM'
    bpy.context.scene.render.ffmpeg.codec = 'WEBM'
```

## Workflow complet

### 1. Préparation
1. Ouvrir Blender et exécuter le script de configuration
2. Importer l'image 2D du studio MaâtCore
3. Configurer l'éclairage et la caméra

### 2. Import de l'avatar
1. Importer le modèle 3D de l'avatar
2. Positionner dans la zone dédiée
3. Configurer les animations de base

### 3. Synchronisation
1. Récupérer les données audio/vidéo du backoffice
2. Synchroniser l'animation avec l'audio
3. Ajuster le timing et les expressions

### 4. Rendu
1. Configurer les paramètres de rendu
2. Lancer le rendu (manuel ou automatique)
3. Export vers le format de diffusion

### 5. Intégration
1. Upload du rendu vers le CDN
2. Mise à jour des URLs dans le backoffice
3. Diffusion via les canaux configurés

## Optimisations

### Performance
- Utiliser des proxies pour les textures haute résolution
- Optimiser le maillage des avatars
- Utiliser le GPU rendering (CUDA/OpenCL)
- Cache des animations répétitives

### Qualité
- Éclairage HDRI pour un rendu photoréaliste
- Subsurface scattering pour la peau
- Motion blur pour les mouvements naturels
- Anti-aliasing adaptatif

### Automatisation
- Scripts Python pour les tâches répétitives
- Queue de rendu automatique
- Monitoring des performances
- Backup automatique des projets

## Dépannage

### Problèmes courants

1. **Avatar mal positionné**
   - Vérifier les coordonnées de la zone avatar
   - Ajuster l'échelle et la rotation

2. **Éclairage incorrect**
   - Recalibrer les lumières
   - Vérifier les ombres et reflets

3. **Synchronisation audio décalée**
   - Vérifier le framerate du projet
   - Recalculer les keyframes audio

4. **Rendu lent**
   - Réduire les samples Cycles
   - Utiliser le denoising
   - Optimiser la géométrie

### Logs et monitoring
```python
import logging

# Configuration des logs
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('blender_maatcore.log'),
        logging.StreamHandler()
    ]
)

def log_render_progress(frame, total_frames):
    progress = (frame / total_frames) * 100
    logging.info(f"Rendu en cours: {frame}/{total_frames} ({progress:.1f}%)")
```

## Conclusion

Cette intégration permet de créer des présentations de nouvelles immersives avec des avatars 3D dans l'environnement du studio MaâtCore. Le système est conçu pour être automatisé et s'intégrer parfaitement avec le backoffice existant.

Pour une mise en œuvre complète, il est recommandé de :
1. Tester d'abord avec un avatar simple
2. Optimiser progressivement la qualité
3. Automatiser le pipeline de production
4. Monitorer les performances en continu