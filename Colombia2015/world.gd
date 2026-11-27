extends Node3D
const TILE = 128
const STEP = 8
var tiles: Dictionary = {}
var noise := FastNoiseLite.new()
var camera := Camera3D.new()
var sun := DirectionalLight3D.new()
var terrain_material := StandardMaterial3D.new()
var road_material := StandardMaterial3D.new()
var clock := 10.0
var touch := Vector2.ZERO
func road_x(z: float) -> float:
	return sin(z * 0.006) * 45.0
func elevation(x: float, z: float) -> float:
	var distance := absf(x - road_x(z))
	var base := sin(z * 0.002) * 24.0
	return base + smoothstep(5.0, 70.0, distance) * (35.0 + noise.get_noise_2d(x, z) * 45.0)
func _ready() -> void:
	noise.seed = 2015
	noise.frequency = 0.012
	terrain_material.albedo_color = Color(0.19, 0.32, 0.12)
	road_material.albedo_color = Color(0.12, 0.13, 0.14)
	road_material.roughness = 0.92
	var env := Environment.new()
	var sky := Sky.new()
	sky.sky_material = ProceduralSkyMaterial.new()
	env.sky = sky
	env.background_mode = Environment.BG_SKY
	env.ambient_light_source = Environment.AMBIENT_SOURCE_SKY
	env.tonemap_mode = Environment.TONE_MAPPER_FILMIC
	env.fog_enabled = true
	env.fog_density = 0.001
	var we := WorldEnvironment.new()
	we.environment = env
	add_child(we)
	add_child(sun)
	sun.shadow_enabled = true
	add_child(camera)
	camera.current = true
	camera.position = Vector3(0, 8, 0)
	camera.rotation_degrees.x = -8
	refresh()
func mesh_from(vertices: PackedVector3Array, indices: PackedInt32Array, material: Material) -> MeshInstance3D:
	var st := SurfaceTool.new()
	st.begin(Mesh.PRIMITIVE_TRIANGLES)
	for v in vertices:
		st.add_vertex(v)
	for i in indices:
		st.add_index(i)
	st.generate_normals()
	var node := MeshInstance3D.new()
	node.mesh = st.commit()
	node.material_override = material
	return node
func make_tile(key: Vector2i) -> Node3D:
	var root := Node3D.new()
	var vertices := PackedVector3Array()
	var indices := PackedInt32Array()
	var n := TILE / STEP
	for j in range(n + 1):
		for i in range(n + 1):
			var x := float(key.x * TILE + i * STEP)
			var z := float(key.y * TILE + j * STEP)
			vertices.append(Vector3(x, elevation(x,z), z))
	for j in range(n):
		for i in range(n):
			var a: int = j * (n + 1) + i
			indices.append_array(PackedInt32Array([a,a+1,a+n+1,a+1,a+n+2,a+n+1]))
	root.add_child(mesh_from(vertices, indices, terrain_material))
	if abs(key.x) <= 1:
		var rv := PackedVector3Array()
		var ri := PackedInt32Array()
		for j in range(TILE / 2 + 1):
			var z := float(key.y * TILE + j * 2)
			var x := road_x(z)
			for side in [-1,1]:
				rv.append(Vector3(x + side * 4.0, elevation(x,z) + 0.25,z))
			if j > 0:
				var a := (j-1)*2
				ri.append_array(PackedInt32Array([a,a+1,a+2,a+1,a+3,a+2]))
		if key.x == 0:
			root.add_child(mesh_from(rv,ri,road_material))
	add_child(root)
	return root
func refresh() -> void:
	var center := Vector2i(floori(camera.position.x/TILE),floori(camera.position.z/TILE))
	for y in range(center.y-2,center.y+3):
		for x in range(center.x-2,center.x+3):
			var key := Vector2i(x,y)
			if not tiles.has(key):
				tiles[key] = make_tile(key)
	for key in tiles.keys():
		if absi(key.x-center.x)>2 or absi(key.y-center.y)>2:
			tiles[key].queue_free()
			tiles.erase(key)
func _input(event: InputEvent) -> void:
	if event is InputEventScreenTouch:
		touch.y = -1.0 if event.pressed else 0.0
func _process(delta: float) -> void:
	var speed := touch.y + Input.get_axis("ui_up","ui_down")
	camera.position.z += speed * 24.0 * delta
	camera.position.x = road_x(camera.position.z)
	camera.position.y = elevation(camera.position.x,camera.position.z) + 5.0
	camera.rotation.y = atan2(road_x(camera.position.z)-road_x(camera.position.z-10.0),10.0)
	clock = fmod(clock + delta * 0.015,24.0)
	sun.rotation_degrees = Vector3(-clock * 15.0 + 90.0,-35.0,0.0)
	refresh()
