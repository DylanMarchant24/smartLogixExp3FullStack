const [form, setForm] = useState({
  codigo: '',
  nombre: '',
  direccion: '',
  comuna: '',
  ciudad: '',
  telefono: '',
  horarioAtencion: '',
  tipo: 'TIENDA_FISICA'
});

<select
  className="input"
  value={form.tipo}
  onChange={cambiarCampo('tipo')}
>
  <option value="TIENDA_FISICA">
    Tienda Física
  </option>

  <option value="BODEGA_CENTRAL">
    Bodega Central
  </option>

  <option value="PUNTO_RETIRO">
    Punto de Retiro
  </option>
</select>