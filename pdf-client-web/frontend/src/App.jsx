import { useState, useRef } from "react";
import * as api from "./api";

const SERVICES = [
  {
    id: "fusionner",
    icon: "⊕",
    label: "Fusionner",
    desc: "Assemblez deux fichiers PDF en un seul document",
    color: "#4F7FFF",
    gradient: "linear-gradient(135deg, #4F7FFF, #7C3AED)",
  },
  {
    id: "decouper",
    icon: "✂",
    label: "Découper",
    desc: "Extrayez une plage de pages de votre PDF",
    color: "#10B981",
    gradient: "linear-gradient(135deg, #10B981, #0891B2)",
  },
  {
    id: "extraire",
    icon: "◈",
    label: "Extraire",
    desc: "Isolez une page spécifique du document",
    color: "#F59E0B",
    gradient: "linear-gradient(135deg, #F59E0B, #EF4444)",
  },
  {
    id: "supprimer",
    icon: "⊖",
    label: "Supprimer",
    desc: "Retirez une page indésirable",
    color: "#EF4444",
    gradient: "linear-gradient(135deg, #EF4444, #7C3AED)",
  },
  {
    id: "motdepasse",
    icon: "⊛",
    label: "Protéger",
    desc: "Chiffrez votre PDF avec un mot de passe AES-128",
    color: "#8B5CF6",
    gradient: "linear-gradient(135deg, #8B5CF6, #EC4899)",
  },
  {
    id: "image",
    icon: "⬡",
    label: "PDF → Image",
    desc: "Convertissez une page en image PNG haute qualité",
    color: "#0EA5E9",
    gradient: "linear-gradient(135deg, #0EA5E9, #10B981)",
  },
  {
    id: "texte",
    icon: "≡",
    label: "Texte",
    desc: "Extrayez tout le contenu textuel du document",
    color: "#6366F1",
    gradient: "linear-gradient(135deg, #6366F1, #0EA5E9)",
  },
  {
    id: "creer",
    icon: "✦",
    label: "Créer",
    desc: "Générez un nouveau PDF depuis du texte",
    color: "#EC4899",
    gradient: "linear-gradient(135deg, #EC4899, #F59E0B)",
  },
];

function FileZone({ label, file, onChange, accept = ".pdf" }) {
  const ref = useRef();
  return (
    <div
      onClick={() => ref.current.click()}
      style={{
        border: `2px dashed ${file ? "#4F7FFF" : "rgba(255,255,255,0.15)"}`,
        borderRadius: 12,
        padding: "20px 16px",
        cursor: "pointer",
        textAlign: "center",
        background: file ? "rgba(79,127,255,0.08)" : "rgba(255,255,255,0.03)",
        transition: "all 0.2s",
      }}
    >
      <input
        ref={ref}
        type="file"
        accept={accept}
        style={{ display: "none" }}
        onChange={(e) => onChange(e.target.files[0])}
      />
      <div style={{ fontSize: 28, marginBottom: 8 }}>{file ? "📄" : "⊕"}</div>
      <div style={{ fontSize: 13, color: file ? "#4F7FFF" : "rgba(255,255,255,0.5)", fontFamily: "inherit" }}>
        {file ? file.name : label}
      </div>
    </div>
  );
}

function NumField({ placeholder, value, onChange }) {
  return (
    <input
      type="number"
      min={1}
      placeholder={placeholder}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      style={{
        background: "rgba(255,255,255,0.05)",
        border: "1.5px solid rgba(255,255,255,0.12)",
        borderRadius: 8,
        padding: "10px 14px",
        color: "#fff",
        fontSize: 14,
        fontFamily: "inherit",
        outline: "none",
        width: "100%",
        boxSizing: "border-box",
      }}
    />
  );
}

function TextField({ placeholder, value, onChange, rows = 1, password = false }) {
  const Tag = rows > 1 ? "textarea" : "input";
  return (
    <Tag
      type={password ? "password" : "text"}
      placeholder={placeholder}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      rows={rows}
      style={{
        background: "rgba(255,255,255,0.05)",
        border: "1.5px solid rgba(255,255,255,0.12)",
        borderRadius: 8,
        padding: "10px 14px",
        color: "#fff",
        fontSize: 14,
        fontFamily: "inherit",
        outline: "none",
        width: "100%",
        resize: rows > 1 ? "vertical" : undefined,
        boxSizing: "border-box",
        minHeight: rows > 1 ? 140 : undefined,
      }}
    />
  );
}

function ActionBtn({ label, color, gradient, onClick, loading }) {
  return (
    <button
      onClick={onClick}
      disabled={loading}
      style={{
        background: loading ? "rgba(255,255,255,0.1)" : gradient,
        border: "none",
        borderRadius: 10,
        padding: "12px 28px",
        color: "#fff",
        fontWeight: 700,
        fontSize: 14,
        fontFamily: "inherit",
        cursor: loading ? "not-allowed" : "pointer",
        letterSpacing: "0.03em",
        boxShadow: loading ? "none" : `0 4px 20px ${color}44`,
        transition: "all 0.2s",
        opacity: loading ? 0.6 : 1,
      }}
    >
      {loading ? "⏳ Traitement…" : label}
    </button>
  );
}

export default function App() {
  const [active, setActive] = useState(0);
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState(null);
  const [texte, setTexte] = useState("");

  const [pdf1, setPdf1] = useState(null);
  const [pdf2, setPdf2] = useState(null);
  const [pdf, setPdf] = useState(null);
  const [debut, setDebut] = useState("");
  const [fin, setFin] = useState("");
  const [page, setPage] = useState("");
  const [mdp, setMdp] = useState("");
  const [contenu, setContenu] = useState("");

  const svc = SERVICES[active];

  const run = async (fn) => {
    setLoading(true);
    setStatus(null);
    setTexte("");
    try {
      const r = await fn();
      if (typeof r === "string") {
        setTexte(r);
        setStatus({ ok: true, msg: "Texte extrait avec succès !" });
      } else {
        setStatus({ ok: true, msg: "Opération réussie — fichier téléchargé." });
      }
    } catch (e) {
      setStatus({ ok: false, msg: "Erreur : " + (e?.response?.data || e.message) });
    } finally {
      setLoading(false);
    }
  };

  const panels = [
    // Fusionner
    <div style={S.form}>
      <div style={S.label}>Sélectionnez les deux fichiers PDF à fusionner</div>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
        <FileZone label="Premier PDF" file={pdf1} onChange={setPdf1} />
        <FileZone label="Deuxième PDF" file={pdf2} onChange={setPdf2} />
      </div>
      <ActionBtn label="Fusionner les PDFs" color={svc.color} gradient={svc.gradient}
        loading={loading} onClick={() => run(() => api.fusionner(pdf1, pdf2))} />
    </div>,

    // Découper
    <div style={S.form}>
      <div style={S.label}>Choisissez le PDF et la plage de pages</div>
      <FileZone label="Sélectionner le PDF" file={pdf} onChange={setPdf} />
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
        <NumField placeholder="Page de début" value={debut} onChange={setDebut} />
        <NumField placeholder="Page de fin" value={fin} onChange={setFin} />
      </div>
      <ActionBtn label="Découper" color={svc.color} gradient={svc.gradient}
        loading={loading} onClick={() => run(() => api.decouper(pdf, debut, fin))} />
    </div>,

    // Extraire page
    <div style={S.form}>
      <div style={S.label}>Sélectionnez le PDF et le numéro de page</div>
      <FileZone label="Sélectionner le PDF" file={pdf} onChange={setPdf} />
      <NumField placeholder="Numéro de page" value={page} onChange={setPage} />
      <ActionBtn label="Extraire la page" color={svc.color} gradient={svc.gradient}
        loading={loading} onClick={() => run(() => api.extrairePage(pdf, page))} />
    </div>,

    // Supprimer page
    <div style={S.form}>
      <div style={S.label}>Sélectionnez le PDF et la page à supprimer</div>
      <FileZone label="Sélectionner le PDF" file={pdf} onChange={setPdf} />
      <NumField placeholder="Page à supprimer" value={page} onChange={setPage} />
      <ActionBtn label="Supprimer la page" color={svc.color} gradient={svc.gradient}
        loading={loading} onClick={() => run(() => api.supprimerPage(pdf, page))} />
    </div>,

    // Mot de passe
    <div style={S.form}>
      <div style={S.label}>Chiffrez votre PDF avec un mot de passe</div>
      <FileZone label="Sélectionner le PDF" file={pdf} onChange={setPdf} />
      <TextField placeholder="Entrez le mot de passe" value={mdp} onChange={setMdp} password />
      <ActionBtn label="Protéger le PDF" color={svc.color} gradient={svc.gradient}
        loading={loading} onClick={() => run(() => api.ajouterMotDePasse(pdf, mdp))} />
    </div>,

    // PDF → Image
    <div style={S.form}>
      <div style={S.label}>Convertissez une page en image PNG (150 DPI)</div>
      <FileZone label="Sélectionner le PDF" file={pdf} onChange={setPdf} />
      <NumField placeholder="Numéro de page" value={page} onChange={setPage} />
      <ActionBtn label="Convertir en image" color={svc.color} gradient={svc.gradient}
        loading={loading} onClick={() => run(() => api.convertirImage(pdf, page))} />
    </div>,

    // Extraire texte
    <div style={S.form}>
      <div style={S.label}>Extrayez tout le texte du document PDF</div>
      <FileZone label="Sélectionner le PDF" file={pdf} onChange={setPdf} />
      <ActionBtn label="Extraire le texte" color={svc.color} gradient={svc.gradient}
        loading={loading} onClick={() => run(() => api.extraireTexte(pdf))} />
      {texte && (
        <textarea readOnly value={texte} style={{
          ...S.textarea,
          borderColor: svc.color + "44",
        }} />
      )}
    </div>,

    // Créer PDF
    <div style={S.form}>
      <div style={S.label}>Rédigez votre contenu et générez un PDF</div>
      <TextField placeholder="Tapez votre contenu ici…" value={contenu}
        onChange={setContenu} rows={6} />
      <ActionBtn label="Créer le PDF" color={svc.color} gradient={svc.gradient}
        loading={loading} onClick={() => run(() => api.creerPDF(contenu))} />
    </div>,
  ];

  return (
    <div style={S.root}>
      {/* Fond décoratif */}
      <div style={S.bgOrb1} />
      <div style={S.bgOrb2} />

      {/* Header */}
      <header style={S.header}>
        <div style={S.logoArea}>
          <div style={S.logoIcon}>⬡</div>
          <div>
            <div style={S.logoTitle}>Gestionnaire PDF</div>
            <div style={S.logoSub}>Système distribué · CORBA + PDFBox</div>
          </div>
        </div>
        <div style={S.badge}>8 services</div>
      </header>

      {/* Grille de services */}
      <div style={S.grid}>
        {SERVICES.map((s, i) => (
          <button
            key={s.id}
            onClick={() => { setActive(i); setStatus(null); setTexte(""); }}
            style={{
              ...S.card,
              background: active === i
                ? s.gradient
                : "rgba(255,255,255,0.04)",
              border: active === i
                ? "1.5px solid transparent"
                : "1.5px solid rgba(255,255,255,0.08)",
              boxShadow: active === i ? `0 8px 32px ${s.color}55` : "none",
              transform: active === i ? "translateY(-2px)" : "none",
            }}
          >
            <div style={{ fontSize: 26, marginBottom: 8 }}>{s.icon}</div>
            <div style={{ fontWeight: 700, fontSize: 14, marginBottom: 4 }}>{s.label}</div>
            <div style={{
              fontSize: 11,
              color: active === i ? "rgba(255,255,255,0.8)" : "rgba(255,255,255,0.4)",
              lineHeight: 1.4,
            }}>{s.desc}</div>
          </button>
        ))}
      </div>

      {/* Panneau actif */}
      <div style={{ ...S.panel, borderColor: svc.color + "33" }}>
        <div style={S.panelHeader}>
          <span style={{ ...S.panelIcon, background: svc.gradient }}>{svc.icon}</span>
          <div>
            <div style={S.panelTitle}>{svc.label}</div>
            <div style={S.panelDesc}>{svc.desc}</div>
          </div>
        </div>

        {panels[active]}

        {status && (
          <div style={{
            ...S.statusBox,
            background: status.ok ? "rgba(16,185,129,0.12)" : "rgba(239,68,68,0.12)",
            borderColor: status.ok ? "#10B981" : "#EF4444",
            color: status.ok ? "#10B981" : "#EF4444",
          }}>
            {status.ok ? "✓" : "✗"} {status.msg}
          </div>
        )}
      </div>

      <footer style={S.footer}>
        Gestionnaire PDF · Architecture distribuée CORBA · Apache PDFBox 2.x
      </footer>
    </div>
  );
}

const S = {
  root: {
    minHeight: "100vh",
    background: "#0A0A0F",
    fontFamily: "'DM Sans', 'Outfit', 'Segoe UI', sans-serif",
    color: "#fff",
    padding: "0 0 60px",
    position: "relative",
    overflow: "hidden",
  },
  bgOrb1: {
    position: "fixed", top: -200, right: -200,
    width: 600, height: 600, borderRadius: "50%",
    background: "radial-gradient(circle, rgba(79,127,255,0.12) 0%, transparent 70%)",
    pointerEvents: "none",
  },
  bgOrb2: {
    position: "fixed", bottom: -150, left: -150,
    width: 500, height: 500, borderRadius: "50%",
    background: "radial-gradient(circle, rgba(236,72,153,0.08) 0%, transparent 70%)",
    pointerEvents: "none",
  },
  header: {
    display: "flex", alignItems: "center", justifyContent: "space-between",
    padding: "28px 48px",
    borderBottom: "1px solid rgba(255,255,255,0.06)",
    backdropFilter: "blur(20px)",
    position: "sticky", top: 0, zIndex: 100,
    background: "rgba(10,10,15,0.8)",
  },
  logoArea: { display: "flex", alignItems: "center", gap: 16 },
  logoIcon: {
    fontSize: 32, color: "#4F7FFF",
    background: "rgba(79,127,255,0.1)",
    borderRadius: 12, padding: "8px 12px",
    lineHeight: 1,
  },
  logoTitle: { fontSize: 22, fontWeight: 800, letterSpacing: "-0.03em" },
  logoSub: { fontSize: 12, color: "rgba(255,255,255,0.4)", marginTop: 2 },
  badge: {
    background: "rgba(79,127,255,0.15)",
    border: "1px solid rgba(79,127,255,0.3)",
    color: "#4F7FFF", borderRadius: 20,
    padding: "4px 14px", fontSize: 12, fontWeight: 600,
  },
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(180px, 1fr))",
    gap: 12,
    padding: "32px 48px 0",
    maxWidth: 1100, margin: "0 auto",
  },
  card: {
    borderRadius: 14, padding: "20px 16px",
    cursor: "pointer", textAlign: "left",
    transition: "all 0.22s cubic-bezier(0.4,0,0.2,1)",
    color: "#fff", fontFamily: "inherit",
  },
  panel: {
    maxWidth: 680, margin: "28px auto 0",
    background: "rgba(255,255,255,0.03)",
    border: "1.5px solid",
    borderRadius: 20, padding: "28px 32px",
    backdropFilter: "blur(10px)",
  },
  panelHeader: {
    display: "flex", alignItems: "center",
    gap: 16, marginBottom: 24,
    paddingBottom: 20,
    borderBottom: "1px solid rgba(255,255,255,0.07)",
  },
  panelIcon: {
    fontSize: 24, borderRadius: 12,
    padding: "10px 14px", lineHeight: 1,
  },
  panelTitle: { fontSize: 20, fontWeight: 800, letterSpacing: "-0.02em" },
  panelDesc: { fontSize: 13, color: "rgba(255,255,255,0.5)", marginTop: 3 },
  form: { display: "flex", flexDirection: "column", gap: 14 },
  label: { fontSize: 13, color: "rgba(255,255,255,0.5)", marginBottom: 4 },
  textarea: {
    background: "rgba(255,255,255,0.04)",
    border: "1.5px solid",
    borderRadius: 10, padding: "14px",
    color: "#fff", fontSize: 13,
    fontFamily: "inherit", resize: "vertical",
    minHeight: 140, outline: "none",
    width: "100%", boxSizing: "border-box",
  },
  statusBox: {
    marginTop: 16, padding: "12px 16px",
    borderRadius: 10, border: "1px solid",
    fontSize: 13, fontWeight: 500,
  },
  footer: {
    textAlign: "center", marginTop: 48,
    fontSize: 12, color: "rgba(255,255,255,0.2)",
    letterSpacing: "0.05em",
  },
};
