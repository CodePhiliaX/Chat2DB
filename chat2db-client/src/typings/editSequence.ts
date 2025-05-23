import { EditColumnOperationType, NullableType } from '@/constants';

export interface ISequenceInfo {
    nspname: String,
    relname: string;
    comment?: string | null;
    typname?: string | null;
    seqcache?: number | null;
    rolname?: string | null;
    seqstart?: number | null;
    seqincrement?: string | null;
    seqmax?: number | null;
    seqmin?: number | null;
    seqcycle?: Boolean | null;
}