// src/components/ListeCanaux.tsx
import type { Canal } from '../services/canalService';

interface ListeCanauxProps {
    canaux: Canal[];
    selectedCanalId: number | null;
    onSelectCanal: (canal: Canal) => void;
}

export default function ListeCanaux({ canaux, selectedCanalId, onSelectCanal }: ListeCanauxProps) {
    return (
        <div>
            <h4>Liste des canaux</h4>
            <ul>
                {canaux.map((canal) => (
                    <li key={canal.idCanal}>
                        <button
                            type="button"
                            onClick={() => onSelectCanal(canal)}
                            disabled={selectedCanalId === canal.idCanal}
                        >
                            {canal.typeCanal === 'privé' ? '🔒' : '💬'} {canal.nom}
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
}