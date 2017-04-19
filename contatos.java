package com.example.gabi2.agendatxt;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class contatos extends AppCompatActivity {
    private TextView txtNomeArq;
    private TextView txtSalvar;
    private TextView txtLer;
    private Spinner SpnListarArquivos;
    private ArrayList<String> Arquivos = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.contatos);

            final EditText campo_telefone = (EditText) findViewById(R.id.edtSalvar);
            campo_telefone.addTextChangedListener(Mask.insert("(##)####-####", campo_telefone));

            txtNomeArq = (TextView) findViewById(R.id.edtNomeArq);
            txtSalvar = (TextView) findViewById(R.id.edtSalvar);
            txtLer = (TextView) findViewById(R.id.edtLer);
            SpnListarArquivos = (Spinner)  findViewById(R.id.spListarArquivos);

            Listar();

        }
        catch (Exception e)
        {
            Mensagem("Erro : " + e.getMessage());
        }
    }
    private void Mensagem(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    private String ObterDiretorio()
    {
        File root = android.os.Environment.getExternalStorageDirectory();
        return root.toString();
    }

    public void Listar()
    {
        File diretorio = new File(ObterDiretorio());
        File[] arquivos = diretorio.listFiles();
        if(arquivos != null)
        {
            int length = arquivos.length;
            for(int i = 0; i < length; ++i)
            {
                File f = arquivos[i];
                if (f.isFile()){
                    Arquivos.add(f.getName());
                }
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this,android.R.layout.simple_dropdown_item_1line, Arquivos);
            SpnListarArquivos.setAdapter(arrayAdapter);
        }
    }

    public void click_Salvar(View v)
    {
        String lstrNomeArq;
        File arq;
        byte[] dados;

        if (txtNomeArq.getText().toString().trim().isEmpty()) {
            txtNomeArq.setError("Campo Obrigatório");
            txtNomeArq.requestFocus();
        }
        if (txtSalvar.getText().toString().trim().isEmpty()) {
            txtSalvar.setError("Campo Obrigatório");
            txtSalvar.requestFocus();
        }else {
            try {
                lstrNomeArq = txtNomeArq.getText().toString();

                arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
                FileOutputStream fos;

                dados = txtSalvar.getText().toString().getBytes();

                fos = new FileOutputStream(arq);
                fos.write(dados);
                fos.flush();
                fos.close();
                Mensagem("Texto Salvo com sucesso!");
                Listar();
            } catch (Exception e) {
                Mensagem("Erro : " + e.getMessage());
            }
        }
    }

    public void click_Carregar(View v)
    {
        String lstrNomeArq;
        File arq;
        String lstrlinha;
        try
        {
            lstrNomeArq = SpnListarArquivos.getSelectedItem().toString();

            txtLer.setText("");

            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            BufferedReader br = new BufferedReader(new FileReader(arq));

            while ((lstrlinha = br.readLine()) != null)
            {
                if (!txtLer.getText().toString().equals(""))
                {
                    txtLer.append("\n");
                }
                txtLer.append(lstrlinha);
            }

        }
        catch (Exception e)
        {
            Mensagem("Erro : " + e.getMessage());
        }
    }




    public abstract static class Mask {
        public static String unmask(String s) {
            return s.replaceAll("[.]", "").replaceAll("[-]", "")
                    .replaceAll("[/]", "").replaceAll("[(]", "")
                    .replaceAll("[)]", "");
        }

        public static TextWatcher insert(final String mask, final EditText ediTxt) {
            return new TextWatcher() {
                boolean isUpdating;
                String old = "";
                public void onTextChanged(CharSequence s, int start, int before,int count) {
                    String str = Mask.unmask(s.toString());
                    String mascara = "";
                    if (isUpdating) {
                        old = str;
                        isUpdating = false;
                        return;
                    }
                    int i = 0;
                    for (char m : mask.toCharArray()) {
                        if (m != '#' && str.length() > old.length()) {
                            mascara += m;
                            continue;
                        }
                        try {
                            mascara += str.charAt(i);
                        } catch (Exception e) {
                            break;
                        }
                        i++;
                    }
                    isUpdating = true;
                    ediTxt.setText(mascara);
                    ediTxt.setSelection(mascara.length());
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void afterTextChanged(Editable s) {}
            };
        }
    }
}
